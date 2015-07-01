(require '[clojure.data.csv :as csv]
         '[clojure.core.reducers :as r]
         '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[taoensso.carmine :as redis]
         '[taoensso.nippy :as nippy])


(def *total* )
(defn line-seq [filename]
  (let [rdr (io/reader filename)]
    (defn read-next-line[]
      (if-let [line (.readLine rdr)]
        (cons line (lazy-seq (read-next-line)))
        (.close rdr)))
    (lazy-seq (read-next-line))))
(defn pcount [seq]
  (r/fold 2000 + + (map (constantly 1) seq)))
(defn line-seq-file [lines filename]
  (with-open [wtr (io/writer filename)]
    (doseq [line lines]
      (.write wtr (str line "\n") ))))

(defn spy-helper [expr value]
  (println expr value)
  value)
(defmacro spy [x]
  `(spy-helper '~x ~x))
(defn make-head [maps-expr]
  [(into {} (first maps-expr)) 
   (second maps-expr)])
(defmacro domap [maps-expr & body]
  `(doseq ~(make-head maps-expr)
     ~@body))
(defmacro doseq-nth [])
(defmacro pdoseq [[item coll]  & body]
  `(let [procs# (.availableProcessors (Runtime/getRuntime))
         futures# (for [i# (range procs#)]
                    (future
                      #(doseq [~item (take-nth procs# (drop i# ~coll))]
                         ~@body)))]
     (doseq [f# futures#]
       (deref f#))))
(defmacro parallel-doseq [[item coll] & body]
  `(apply await
          (for [a# (map agent ~coll)]
            (send a# (fn [~item] ~@body))
            a#)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defprotocol graph-query
  (in-degree [self x] )
  (out-degree [self y] )
  (com-in-degree [self x] )
  (com-out-degree [self x] )
  (shorted-distance [self x y] ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn make-id-name-map  [line]
  (let [[_ id title] (str/split line #":" 3)]
    {:id (Long/parseLong id)  :title title}) )
(defn id-name-seq
  ([] (id-name-seq "/mnt/windows/data/all.txt"))
  ([filename] (map make-id-name-map (line-seq filename))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defmacro wcar [& body]
  `(redis/wcar {} ~@body))
(defn r-set [n key value]
  (wcar (redis/set (str n ":" key) value)) )
(defn r-get [n key] 
  (wcar (redis/get  (str n ":" key))) )
(defmacro r-pget [n keys]
  `(wcar ~@(map #(redis/get (str  n ":"  %1)) keys)))
(defn r-save-nameid [id-name-maps]
  (doseq [{id :id title :title} id-name-maps] 
    (r-set "nameid" title id)))
(defn r-save-idname [id-name-maps]
  (doseq [{id :id title :title} id-name-maps] 
    (r-set "idname" title id)))
(defn nameid [id-name-maps]
  (r/reduce #(assoc %1 (:title %2) (:id %2)) {} id-name-maps))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn read-bits[lines]
  (defn- ->long[line]
    (let [[t src dest](str/split line #",")
          src-id (Long/parseLong src)
          dest-id (Long/parseLong dest)]
      [src-id dest-id]))
  (doseq [[src dest] (map ->long lines)]
    (wcar (redis/setbit ":nodes" src 1)
          (redis/setbit ":nodes" dest 1))))
(defn read-file-bits [filename ]
  (with-open [rdr (io/reader filename)]
    (read-bits (line-seq rdr))))
(defn count-bit1 [bits]
  (defn- bits7 [n]
    (loop [total 0 t n]
      (if (zero? t)
        total
        (recur (+ total 1) (bit-and t (- t 1))))))
  (r/fold + + (r/map #(bits7 (int %1))  bits)))
(defn bitcount [bits]
  (wcar (redis/bitcount bits)))
(defn node-seq1 [bits]
  (filter #(= 1 (wcar (redis/getbit ":nodes" %1))) 
          (range 0 (size-bit bits))))


(defn- node-contain? [x]
    (= 1 (wcar (redis/getbit ":nodes" x))))
(defn- bitpos [x]
  (wcar (redis/bitpos ":nodes" 1 x x)))
(defn- bitpos-e [x]
  (wcar (redis/bitpos ":nodes" 1 x )))
(defn round8-up [x]
  (bit-shift-left (+ 1 (bit-shift-right x 3)) 3))
(defn node-count [n]
  (loop [next (bitpos-e n) total 0]
    (if (= -1 next) total
        (recur (/ (round8-up next) 8)
               (+ total (let [number 0]
                          (doseq [x (range next (round8-up next))]
                            (if (node-contain? x) (inc  number)) )
                          number))))))
(defn node-seq
  ([] (node-seq 0))
  ([n]
   (let [next (bitpos-e n)]
     (if (= -1 next) nil
         (lazy-cat (filter node-contain? (range next (round8-up next)))
                   (node-seq (/ (round8-up next) 8)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *m* {"LinkTo" 0 "RedirectTo" 1})
(def *nameid* nil)
(defn getid [str]
  (*nameid* str))
                                        ;[src-id dest-id ]
(defn id-link [line] 
  (let [[type src dest text] (str/split line #",")
             [src-id dest-id ] (wcar
                                (redis/get (str "nameid" ":" src))
                                (redis/get (str "nameid" ":" dest)))]
    (when (and src-id dest-id)
      (str (*m* type)  "," src-id "," dest-id "," text))))

(defn map-file
  ([] (map-file id-link "/mnt/windows/data/link.cvs"
                "/mnt/windows/data/id-link.cvs" ))
  ([fun infile outfile]
   (line-seq-file (filter (complement nil?)
                          (map fun (line-seq infile))) outfile)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(r/map #(str/split % #"," ) (take 10 links))

