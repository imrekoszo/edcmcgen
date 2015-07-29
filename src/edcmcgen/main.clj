(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [clojure.string :as s]))

(defn -main [filename]
  (->> "../../Documents/ch-products-elite-map/config/Custom.binds"
       translate-binds
       :mapped-to-keys
       (map format-key-mapping)
       sort
       (s/join "\n")
       println
       ))
