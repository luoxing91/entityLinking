(defn make-point [x y z ]
  {:x x :y y :z z })
(defn square [x ]                                       ;
  (Math/pow x 2))
(defn sqrt [x]
  (Math/sqrt x))
(defn distance [p1 p2]
  (sqrt (+ (square (- (:x p1) (:x p2)) )
           (square (- (:y p1) (:y p2))) 
           (square  (- (:z p1) (:z p2))) )) )
(defn count-neighbour [p points dc]
  (count (filter #(distance p %) points)))
(defn count-neighbour-index [index points dc]
  (count-neighbour (nth points index) points dc))
(defn local-density [points dc]
  (let [n (count points)]
    (loop [vec1 (vec (repeat n 0)) 
           x 0]
      (cond (>= x n  ) vec1
            :else (recur (assoc vec1 x
                                   (count-neighbour (nth points x)
                                                    points dc  ))
                         (inc x))))))
(defn distance-to-higher-Density [points rho]
  (let [size (count points)
        delta (vec (repeat size 0.0))]
    delta))
