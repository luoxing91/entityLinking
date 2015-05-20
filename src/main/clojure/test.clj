(ns tk.luoxing123.clojure
  ;(:refer-clojure
  (:require (clojure [string :as string]
                     [set :as set])
           )
  (:import (java.util ArrayList Date)
           (java.io File)
           (org.apache.lucene  document.Document
                             index.IndexReader
                             store.FSDirectory
                             store.Directory
                             index.DirectoryReader)
           tk.luoxing123.corpus.goldCollection
           (tk.luoxing123.utils LuceneDocIterator)
           (tk.luoxing123.graph Graph NodeFactory) )
  )

                                        ;(import 'tk.luoxing123.utils.LucenDocIterator)
(import 'java.io.File)
(import 'java.util.ArrayList)


(import 'tk.luoxing123.utils.LuceneDocIterator)
(import 'tk.luoxing123.graph.NodeFactory)
(import 'tk.luoxing123.graph.Node)
(import 'tk.luoxing123.graph.Graph)
(import 'tk.luoxing123.corpus.goldCollection)
(import 'org.apache.lucene.analysis.Analyzer) 
(import 'org.apache.lucene.analysis.standard.StandardAnalyzer) 
(import 'org.apache.lucene.document.Document) 
(import 'org.apache.lucene.document.Field)
(import 'org.apache.lucene.document.Field$Store)
(import 'org.apache.lucene.document.LongField) 
(import 'org.apache.lucene.document.StringField)
(import 'org.apache.lucene.document.DoubleField)
(import 'org.apache.lucene.document.TextField) 
(import 'org.apache.lucene.index.IndexWriter) 
(import 'org.apache.lucene.index.IndexWriterConfig$OpenMode) 
(import 'org.apache.lucene.index.IndexWriterConfig)
(import 'org.apache.lucene.index.Term)
(import 'org.apache.lucene.store.Directory)
(import 'org.apache.lucene.store.FSDirectory) 
(import 'org.apache.lucene.util.Version) 
(import 'java.io.File)
(import 'tk.luoxing123.entitylink.MentionFactory)
(import 'org.apache.commons.io.FileUtils)

(require '[clojure.data.xml :as xml])

(defn words [text] (re-seq #"[a-z]+" (.toLowerCase text)))
(defn train [features]
  (reduce (fn [model f] (assoc model f (inc (get model f 1))))
          {}
          features))






(defn write-document-index [doc-lst directory ]
  (let [writer (IndexWriter.  (FSDirectory/open directory)
                              (.setOpenMode
                               (IndexWriterConfig. Version/LUCENE_4_9
                                                   (StandardAnalyzer.
                                                    Version/LUCENE_4_9)) 
                               IndexWriterConfig$OpenMode/CREATE ))]
    (doseq [ doc doc-lst]
      (.addDocument writer doc ) )
    (.close writer)))

;(def *reader* (DirectoryReader/open
;               (FSDirectory/open  (File. "/home/luoxing/mentions"))))
(defn iterator->arrayList [iter] 
  (let [lst (iterator-seq iter)
        rest (ArrayList.)]
    (doseq [value lst]
      (.add  rest lst ))
    rest))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;lucene tools 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
(defn seq-document[str]
  ( iterator-seq  (LuceneDocIterator. str)))


(defn addNode [node writer]
  (.addDocument writer (node->document node)))
(defn addMention [mention writer]
  (.addDocument writer (mention->document mention)))
(defn writeToLucene  [lst dir]
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def docs (take 5  (seq-document "/home/luoxing/mentions")))
(def document (first docs))
(def xmlDoc (mentions->xml docs))
(def t (xml/emit xmlDoc ))
(def XML1 (xml/emit-str (mentionToxml document 1)))
(def query
  (xml/element :query {:id "ok "}
               (xml/element :name {} "name")
               (xml/element :docid {} "fileId")
               (xml/element :beg  {} "2")
               (xml/element :end  {}"2")))
(xml/emit-str query)

(def articleDir (File. "/home/luoxing/windows/test/evaluation"))
(def indexDir (File. "/home/luoxing/eval"))
(def evaluation-article (.listFiles articleDir))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def mention-factory (MentionFactory.))
(defn article->mentions [article]
  (iterator-seq (.mentionIterator
                 mention-factory
                 (FileUtils/readFileToString article "utf-8")
                 (.getName article))))
(defn article->ner-mentions [article]
  (filter #(not  (or (.equals "NUMBER" (.getNer %1))
                     (.equals "DATE" (.getNer %1))) )
          (article->mentions- article)))
(defn files->mentions [files]
  (if (seq files)
    (lazy-cat (article->ner-mentions (first files))
              (files->mentions (rest files)))))

(defn directory->mentions [dir]
  (files->mentions (.listFiles dir)))
(defn article->mentions- [article]
  (do (println (.getName article)               )
      (article->mentions article)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-mention-start [mention]
  (.longValue (Integer. (.getStart mention))))

(defn mention->document [mention]
  (doto (Document.)
    (.add (TextField. "name" (.getName mention)
                      Field$Store/YES))
    (.add (TextField. "ner"  (.getNer mention)
                      Field$Store/YES))
    (.add (LongField. "start"
                      (get-mention-start mention )
                      Field$Store/YES))
    (.add (TextField. "fileId"
                      (.getArticleId mention)
                      Field$Store/YES))))

(defn main-make-mentions [article-dir index-dir]
  (write-document-index
   (map mention->document (directory->mentions article-dir))
   index-dir))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;To Node
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn make-gold []
  (-> (goldCollection/createInstance )
      (.getGoldNodeIterableNotNIL )
      (.iterator )
      (iterator-seq)))
(defn make-graph [train can ]
  (Graph. (ArrayList. train)
          (ArrayList. can)))
(defn make-result [train can]
  (-> (make-graph train candidates)
      (.toResultIterable)
      (.iterator)
      (iterator-seq)))

(def train
  (make-gold))
(def node-factory
  (NodeFactory. ))
(def *mentions*
  (iterator-seq
   (MentionFactory/makeMentionIterator  "/home/luoxing/eval")) )
(import '(org.apache.lucene.search IndexSearcher
                                   Query
                                   ScoreDoc
                                   TopDocs))                            ;

;import org.apache.lucene.store.Directory;

(defn make-searcher [indexStr]
  (IndexSearcher. (DirectoryReader/open
                   (FSDirectory/open (File. indexStr)))))
(import '(org.apache.lucene.queryparser.classic ParseException
                                                QueryParser)   ) 

(defn make-query-parser []
  (QueryParser. Version/LUCENE_4_9
                "content"
                (StandardAnalyzer. Version/LUCENE_4_9)) )
(defn sum-score [hits]
  (apply + (map #(.score %1) hits)))
(defn get-nodes [searcher parser mention]
  (let [results (.search searcher
                         (.parse parser (.getName mention)  )
                         10)
        hits (.scoreDocs results)
        sumScore (sum-score hits)]
    (loop [i 0 lst [] ]
      (cond (or (>= i 10 ) (>= i (.totalHits results) ))
            lst
            :else
            (let [doc (.doc searcher (.doc (nth hits i )) )]
              (recur (inc i ) (conj lst (Node. mention
                                               (.get doc "id") 
                                                (/ (.score (nth hits i))
                                                   sumScore))))))))) 
(defn mention->nodes [mention ]
  (get-nodes *searcher* *parser*  mention))
(defn mentions->nodes [mentions ]
  (let [mention-ner (filter #(not (.equals (.getNer %1) "TIME"))
                            mentions)]
    (if (seq mention-ner)
      (lazy-cat (mention->nodes (first mention-ner))
                (mentions->nodes (rest mention-ner))))))
(defn node-file-id [node ]
  (.getArticleId (.getMention node)))
(defn get-node-start [node ]
  (get-mention-start (.getMention node)))

(defn node->document [node ]
  (doto (Document.)
    (.add (StringField. "name" (.getName node)
                        Field$Store/YES))
    (.add (StringField. "ner"  (.getNer node)
                        Field$Store/YES))
    (.add (LongField. "start"
                      (get-node-start node)
                      Field$Store/YES))
    (.add (StringField. "fileId"
                        (node-file-id node)
                        Field$Store/YES))
    (.add (StringField. "enitityId"
                        (.getEntityId node)
                        Field$Store/YES))
    (.add (DoubleField. "pro" (.getPopularity
                               node )
                        Field$Store/YES))))




(defn writer-node-index [lst dir] 
  (write-document-index (map #(do (println (.getName %1))
                                  (node->document %1))
                             lst )
                        (File. dir) ))
(def *result*
  (make-result train (mentions->nodes mentions )))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def train-seq (take 100 train) )
(def candidates-seq (take 100 candidates ))
(def result-seq (-> (make-graph train-seq candidates-seq)
                    (.toResultIterable )
                    (.iterator)
                    (iterator-seq)))

(defn writeMentionToLucene [lst dir]
  (let [writer (IndexWriter.  (FSDirectory/open (File. dir))
                    (.setOpenMode
                     (IndexWriterConfig. Version/LUCENE_4_9
                                         (StandardAnalyzer.
                                          Version/LUCENE_4_9)) 
                     IndexWriterConfig$OpenMode/CREATE ))]
    (doseq [ mention lst]
      (addMention mention writer) )
    (.close writer)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def v [1 2 3])
(def mentions* (make-reader "/home/luoxing/mentions") )

