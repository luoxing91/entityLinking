
(require '[clojure.data.xml :as xml])
(defn istag? [i k]
  (= (:tag i) k))

(defn- getText [i]
  (first (:content (first (filter #(istag? %1 :text ) i) )) ) )
(defn elem->map [elem]
  (reduce merge {}
          (map  #(cond
                   (= (:tag %1) :id) {:id (first (:content %1))}
                   (= (:tag %1) :title) {:title (first (:content %1))}
                   (= (:tag %1) :revision)
                   {:text (getText (:content %1)  ) }) 
                (:content elem) )))
(defn page-seq [rdr]
  (filter #(= (:tag %1) :page) (:content (xml/parse rdr)) ))
(defn elem-seq [pages]
  (map elem->map pages))


      

