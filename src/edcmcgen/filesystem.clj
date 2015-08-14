(ns edcmcgen.filesystem
  (:import [java.io.File]))

(defn existing-file? [s]
  (let [f (clojure.java.io/as-file s)]
    (and (.exists f)
         (not (.isDirectory f)))))
