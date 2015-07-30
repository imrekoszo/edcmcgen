(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [clojure.string :as s]
            [clojure.tools.cli :as cli]
            [edcmcgen.cli :refer :all]
            [clojure.edn :as edn])
  (:gen-class))

(defn format-key-mapping [[command key]]
  (format "%1$-35s %2$s" (name command) key))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn value-map
  "Produce a map where for every [k v] of m, [k (f v)] is part of the new map"
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn translate-macro [mac dict]
  (s/join " " (map #(get dict % %) mac)))

(defn get-translated-macros [macro-file keybindings]
  (when macro-file
    (let [macros (edn/read-string (slurp macro-file))]
      (value-map #(translate-macro % keybindings) macros))))

(defn get-extra-mappings [extras-file]
  (if extras-file (slurp extras-file) ""))

(defn -main [& args]
 (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
   (cond
     (:help options) (exit 0 (usage summary))
     (not= 1 (count arguments)) (exit 1 (usage summary))
     (not (existing-file? (first arguments))) (exit 1 (error-msg ["Invalid binding file path"]))
     errors (exit 1 (error-msg errors)))

   (let [bindings-file (first arguments)
         info (translate-binds bindings-file)
         translated-macros (get-translated-macros (:macro-file options) (:mapped-to-keys info))
         mk (->> info :mapped-to-keys (sort-by first) (map format-key-mapping) (s/join \newline))
         mm (->> translated-macros (sort-by first) (map format-key-mapping) (s/join \newline))
         mj (->> info :mapped-to-joy (map format-key-mapping) (map #(str "// " %)) sort (s/join \newline))
         nm (->> info :not-mapped (map name) (map #(str "// " %)) sort (s/join \newline))
         extra (get-extra-mappings (:extra-commands-file options))]
     (do (println "// Commands mapped to keys ----------------------------------------------------")
         (println mk)
         (println)
         (println "// Macros ---------------------------------------------------------------------")
         (println mm)
         (println)
         (println "// Extra commands -------------------------------------------------------------")
         (println extra)
         (println)
         (println "// Commands mapped to joystick buttons ----------------------------------------")
         (println mj)
         (println)
         (println "// Commands not mapped --------------------------------------------------------")
         (println nm))
     )
   )
  )
