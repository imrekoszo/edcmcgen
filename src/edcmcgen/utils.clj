(ns edcmcgen.utils)

(defn map-values [m keys f & args]
  (reduce #(apply update %1 %2 f args) m keys))

(defn map-all-values [m f & args]
  (apply map-values m (keys m) f args))

(def into-map (partial into {}))

(defmacro update-if-contains [m k f & args]
  `(let [m# ~m k# ~k]
     (if (contains? m# k#)
       (update m# k# ~f ~@args)
       m#)))
