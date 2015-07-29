(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [clojure.string :as s]))

(defn format-key-mapping [[command key]]
  (format "%1$-35s %2$s" (name command) key))

(defn -main [filename]
  (let [info (translate-binds filename)
        mk (->> info :mapped-to-keys (map format-key-mapping) sort (s/join "\n"))
        mj (->> info :mapped-to-joy (map format-key-mapping) sort (s/join "\n"))
        nm (->> info :not-mapped (map name) sort (s/join "\n"))]
    (do (println "Mapped to keys:\n--------------------------------------------------")
        (println)
        (println mk)
        (println)
        (println)
        (println "Mapped to joystick buttons:\n--------------------------------------------------")
        (println mj)
        (println)
        (println)
        (println "Not mapped:\n--------------------------------------------------")
        (println)
        (println nm)))
  )
