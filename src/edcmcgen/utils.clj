(ns edcmcgen.utils)

(def into-map (partial into {}))

(defmacro update-if-contains [m k f & args]
  `(let [m# ~m k# ~k]
     (if (contains? m# k#)
       (update m# k# ~f ~@args)
       m#)))
