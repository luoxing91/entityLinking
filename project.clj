(defproject clj-xml "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.lucene/lucene-analyzers-common "5.1.0"]
                 [org.apache.lucene/lucene-queryparser "5.1.0"]
                 [org.apache.lucene/lucene-suggest "5.1.0"]
                 [cider/cider-nrepl "0.9.0-SNAPSHOT"]
                 [clojurewerkz/titanium "1.0.0-beta1"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/tools.logging "0.2.3"]
                 [com.taoensso/carmine "2.10.0"]
                 [com.taoensso/timbre "3.4.0"]
                 [intervox/clj-progress "0.2.0"]
                 [aysylu/loom "0.5.0"]
                 [it.unimi.dsi/webgraph-big "3.3.6"]
                 [com.taoensso/nippy "2.8.0"]
                 
                 [com.googlecode.javaewah/JavaEWAH "1.0.2" ]]
  
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :jvm-opts ["-Xmx3g"])
