;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(import '(java.io StringReader File)
        '(java.nio.file Paths)
        '(org.apache.lucene.analysis Analyzer TokenStream)
        '(org.apache.lucene.analysis.standard StandardAnalyzer)
        '(org.apache.lucene.document Document Field Field$Index Field$Store
                                     NumericField)
        '(org.apache.lucene.index IndexWriter IndexReader Term
                                  IndexWriterConfig DirectoryReader FieldInfo)
        '(org.apache.lucene.queryParser QueryParser)
        '(org.apache.lucene.search BooleanClause BooleanClause$Occur
                                   BooleanQuery IndexSearcher Query ScoreDoc
                                   Scorer TermQuery)
        '(org.apache.lucene.util Version AttributeSource)
        '(org.apache.lucene.store FSDirectory RAMDirectory Directory))
(def ^{:dynamic true} *analyzer* (StandardAnalyzer.))

(defn disk-index
  ([] ( disk-index "/home/luoxing/luceneNeckName")) 
  ([file ] (FSDirectory/open (Paths/get filename (into-array [])))) )

(defn index-reader
  "Create an IndexReader"
  ^IndexReader
  ([] (index-reader (disk-index)))
  ([index] (DirectoryReader/open   index)) )

(defn index-writer
  ^IndexWriter [index]
  (IndexWriter. index (IndexWriterConfig. *analyzer*)))


(defn map->document[map]
  (doto (Document.)
    (.add (LongField. "id" (:node map) Field$Store/YES ))
    (.add (TextField. "title" (:value map) Field$Store/YES ))))

(defn do-index [index  maps]
  (with-open [writer (index-writer index)]
    (doseq [m maps]
      (.addDocument writer (map->document m)))))

(defn lucene-document-seq [filename]
  (let [index (index-reader (disk-index filename)) 
        total (.maxDoc index)]
    (defn read-doc [i]
      (try
        ((.document index i) )
        (catch CorruptIndexException e
          (.close index))))
    (map read-doc (range 0 total))))

(defn make-searcher
  ([] (make-searcher "/home/luoxing/luceneNeckName")) 
  ([file]
   {:search (IndexSearcher. (index-reader (disk-index file)) )
    :parser (QueryParser. "title" *analyzer*)}))

(defn search [index  query-str max-result ]
  (with-open [reader (index-reader index)]
    (let [searcher (IndexSearcher. reader)
          parser (QueryParser. "title" *analyzer*)
          query (.parse parser query-str)
          hits (.search searcher query max-result)
          total (min (.totalHits hits) max-result)]
      (defn l-get [i]
        (aget (.scoreDocs hits) i))
      (map l-get (range 0 total)))))

