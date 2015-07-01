(require '[loom.graph :as graph]
         '[clojure.string :as str]
         '[clojure.set :as set ]
         '[clojure.java.io :as io]
         '[clojure.core.reducers :as r]
         '[clojure.tools.logging :as log])

(import 'com.googlecode.javaewah.EWAHCompressedBitmap)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmacro lx-time [ expr]
  `(with-out-str (time expr)))

(defn pcount [seq]
  (defn- count-seq([] 0)
    ([number x] (inc number)))
  (defn- count-merge([] 0)
    ([& m] (apply + m)))
  (r/fold count-merge count-seq seq))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn word-frequency [text-seq]
  (defn- count-words
    ([] {})
    ([freq word] (assoc freq word (inc (get freq word 0)))))
  (defn- merge-counts
    ([] {})
    ([& m] (apply merge-with + m)))
  (r/fold merge-counts count-words text-seq))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn make-bits [seq]
  (let [seq-do (doall seq)
        bits (EWAHCompressedBitmap. (max seq-do))]
    (doseq [item seq-do]
      (.set bits item))
    bits))


(defn read-bits[lines]
  (defn- ->long[line]
    (let [[t src dest](str/split line #",")
          src-id (Long/parseLong src)
          dest-id (Long/parseLong dest)]
      (if (= src-id dest-id)
        #{src-id}  #{src-id dest-id}) ))
  (defn- sets->bits [sets]
    (let [bits (EWAHCompressedBitmap. 123456789)]
     (doseq [i sets]
       (.set bits i))
     (.trim bits)
     bits))
  (defn- make-bitmap [seq]
    (sets->bits (reduce set/union  seq)))
  (defn- set-or[set1 other]
    (.or (sets->bits other)
         (sets->bits set1)))
  (defn- bitmap-add [bits set1]
    (doseq [i set1]
      (.set bits i))
    bits)
  (defn- redis/setbit [set1]
    (doseq [i set1]
      (set ":nodes" i 1)))
  (defn- bitmap-merge
    ([] (EWAHCompressedBitmap. 123456789))
    ([& maps] (r/reduce #(.or %1 %2) maps )))
  (reduce  bitmap-add (EWAHCompressedBitmap. 123456789)
           (map ->long lines)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn head-line-bits [filename]
  (with-open [rdr (io/reader filename)]
    (read-bits (take 100 (line-seq rdr)))))

