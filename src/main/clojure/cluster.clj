(ns tk.luoxing123.clojure.cluster
  (:require (clojure [string :as string]
                     [set :as set])))
(import 'org.apache.lucene.store.FSDirectory)
(import 'org.apache.lucene.index.DirectoryReader)
(import 'java.io.File)
(require '[clojure.data.xml :as xml])
(require '(clojure [string :as string]
                   [set :as set]))

(defn seq-document [str]
  (let [write (DirectoryReader/open    (FSDirectory/open  (File. str)))]
    (loop [i 0 result [] ]
      (if (>= i (.numDocs write)) result
          (recur (inc i) (cons (.document write i ) result))))))

(defn mention-document->mention [doc] 
  {:fileId (.get doc "fileId")
   :start (.get doc "start")
   :name (.get doc "name")})
(defn link-document->mention [doc ]
  {:fileId (.get doc "fileId")
   :start (.get doc "start")
   :name (.get doc "name")})
(defn select-document [mentions docs]
  (filter #(contains? (set mentions)  (mention-document->mention %1))
          docs))

(defn diff-mention [m-seq l-seq]
  (let [m-docs (set (map mention-document->mention m-seq )) 
        l-docs (set (map link-document->mention l-seq)) ]
    (select-document (set/difference m-docs l-docs  ) m-seq) ))

(defn distance-mention [i j mentions]
  (if (.equals (.get (nth mentions i) "fileId" )
               (.get (nth mentions j) "fileId" ))
    1.0
    0.0))
(import 'tk.luoxing123.entitylink.NameClustering)
(import 'org.la4j.matrix.source.MatrixSource)
(defn clusted [nil-mention]
  (let [n (count nil-mention)
        clustering (NameClustering.
                    (proxy [MatrixSource] []
                      (columns [] n) 
                      (rows [] n ) 
                      ( get [i j ] (do (print i j)
                                       (distance-mention i j nil-mention)) ) )
                    )]
    (.toResult clustering )))

(defn select-mention [indexs mentions]
  (map #(nth mentions %1) indexs))

(defn plane [i set]
  (map #([i %1]) set))
(def counter* 0)
 ;"/home/luoxing/nil"
(defn write [dir ]
  (let  [ mentions (map [#(inc counter* %1 ) #(select-mention %1 nil-mention)]
                        clusted)]
    (write-index-lucene (map nil-link->document (map plane mentions))
                        dir )))

;;;;;;;;;;;
(defn mention-is-linked (mention link)
  (and (.equals (.get mention "fileId")
                (.get link "fileId"))
       (.equals (.get mention "start")
                (.get link "start"))
       (.equals (.get mention "name")
                (.get link "name"))))

(defn seq-contains? [lst item]
  (cond (empty? lst) false
        (mention-is-linked item (first item)) true
        :else (seq-contains? (rest lst) item)))

(def median-value
  (let [values (sort (map #(.get %1 "pro")
                          (seq-document "/home/luoxing/linkResult")) )
        n (count values)]
    (nth values (/ n 2 ))) )
(def nil-mention
  (let [mentions (seq-document "/home/luoxing/eval")
        links (seq-document "/home/luoxing/linkResult")]
    (map #(not (seq-contains? links %1)) mentions)))

