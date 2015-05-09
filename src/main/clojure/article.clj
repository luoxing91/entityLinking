(ns tk.luoxing123.clojure.article
  (:use [clojure.java.io ])
  (:import (tk.luoxing123.corpus ArticleInterface))
  (:gen-class
                                        ;:name tk.luoxing123.clojure.Article
   :implements [tk.luoxing123.corpus.ArticleInterface]
   :method [[getArticleId [] String]
            [getTextContent [] String]
            [getTitleList [] List<String>]
            [toWordFrequencyMap [] Map<String,Integer]]))

(defn -getArticleId []
  "ok")
(defn -getTextContent [] "ok" )
(defn -getTitleList [] nil)
(defn -toWordFrequencyMap [] 0)
