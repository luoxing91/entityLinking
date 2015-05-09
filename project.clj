(defproject clj-xml "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.lucene/lucene-analyzers-common "4.9.0"]
                 [org.apache.lucene/lucene-queryparser "4.9.0"]
                 [org.apache.lucene/lucene-suggest "4.9.0"]
                 [tk.luoxing123.utils/EntityLinking "1.0-SNAPSHOT"]
                 [cider/cider-nrepl "0.8.0-SNAPSHOT"]]
  
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
