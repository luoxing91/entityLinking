(ns tk.luoxing123.clojure.xml
  (:require (clojure [string :as string]
                     [set :set])
            [clojure.data.xml :as xml])
  (:import (java.io File)
           (tk.luoxing123.utils LuceneDocIterator)))
(import 'org.apache.lucene.store.FSDirectory)
(import 'org.apache.lucene.index.DirectoryReader)
(import 'java.io.File)
(require '[clojure.data.xml :as xml])
(require '(clojure [string :as string]
                   [set :as set]))
(defn str->int[str]
  (let [value (read-string str)]
      (if (number? value) value 0)))
(defn seq-document [str]
  (let [write (DirectoryReader/open    (FSDirectory/open  (File. str)))]
    (loop [i 0 result [] ]
      (if (>= i (.numDocs write)) result
          (recur (inc i) (cons (.document write i ) result))))))

(defn link->xml[doc,id ]
  (let [name (.get doc "name")
        start (str->int (.get doc "start"))
        len (.length name)]
    (xml/element :query {:id id}
                 (xml/element :name {} name)
                 (xml/element :docid {} (.get doc "fileId"))
                 (xml/element :beg {} start)
                 (xml/element :end {} (+ start len -1)))))
                                        ;"id"  "reference" "ner" "confidence"
(def map-ner {"PERSON" "PER" 
              "ORGANIZATION" "ORG"
              "LOCATION" "GPE"
              "MISC" "GPE"})
(defn ner [doc]
  (let [v (.get doc "ner")]
    ({"PERSON" "PER" 
      "ORGANIZATION" "ORG"
      "LOCATION" "GPE"
      "MISC" "GPE"} v)))

(defn link->line [doc,id]
  (string/join (interpose "\t" [id (.get doc  "enitityId")
                                (ner doc)
                                (.get doc "pro") ])))
;; (defn links->xml[documents]
;;   (apply xml/element :kbpentlink {}
;;          (map #(link->xml %1 %2) documents ids)))
(defn make-ids []
  (map #(str "EL14_ENG_" (format "%04d" %1)) (range 100000)))

(defn write-link->xml[documents ids ]
  (with-open [out-file (java.io.OutputStreamWriter.
                        (java.io.FileOutputStream. "/home/luoxing/output.xml")
                        "UTF8")] 
    (xml/emit (apply xml/element :kbpentlink {}
                     (map #(link->xml %1 %2) documents ids)  ) out-file) ))
;;;;;;;;;;;
(use 'clojure.java.io)
(defn write-link->tab [documents ids ]
  (with-open [out-file (writer "/home/luoxing/output.tab")
              ]
    (doseq [line (map #(link->line %1 %2 ) documents  ids)]
      (.write out-file (str  line "\n")))))
(defn link->mention [doc]
  {:name (.get doc "name")
   :start (.get doc "start")
   :fileId (.get doc "fileId")})
(defn mention->link [mention docs]
  (first (filter #(= mention (link->mention %1))
                 docs) ) )
(defn unique-documents [docs]
  (pmap #(mention->link %1 docs)
       (distinct (pmap link->mention docs))))
;; (defn unique-documents [documents]
;;   (loop [unique-m (set {}) result [] docs documents]
;;       (cond (empty docs) result
;;             (contains? unique-m (link->mention (first docs)))
;;             (recur unique-m result (rest docs))
;;             :else (recur (.add unique-m (first docs))
;;                          (cons (first docs) result)
;;                          (rest docs)))))
(defn write [documents]
  (let [ids (make-ids)]
    (do (write-link->xml documents ids)
        (write-link->tab documents ids)) ))
