(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [clojure.string :as s]
            [clojure.tools.cli :as cli]
            [edcmcgen.cli :refer :all]
            [clojure.java.io :as io])
  (:gen-class))

(defn format-key-mapping [[command key]]
  (format "%1$-35s %2$s" (name command) key))

(defn exit [status msg]
  (println msg)
  (System/exit status))

; read config
; read input
; process input
;  transform xml into proper data
;  filter data into groups
;  translate e:d keys
;  do data manipulation
; add extra raw data
; write output

(defn get-valid-options [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        elite-bindings-path (first arguments)]
    (do
      (cond
        (:help options) (exit 0 (usage summary))
        (not= 1 (count arguments)) (exit 1 (usage summary))
        (not (existing-file? elite-bindings-path)) (exit 1 (error-msg ["Invalid binding file path"]))
        errors (exit 1 (error-msg errors)))

      {:elite-bindings-path     elite-bindings-path
       :macro-definitions-path  (:macro-file options)
       :static-cmc-content-path (:extra-commands-file options)}))
  )

(defn read-input [{bindings :elite-bindings-path
                   macros :macro-definitions-path
                   static :static-cmc-content-path}]
  {:elite-bindings    (io/input-stream bindings)
   :macro-definitions (when macros (slurp macros))
   :static-cmc        (when static (slurp static))})

(defn -main [& args]
 (let [options (get-valid-options args)
       {:keys [elite-bindings
               macro-definitions
               static-cmc]} (read-input options)]

   (let [info (translate-binds elite-bindings macro-definitions)
         mk (->> info :mapped-to-keys (sort-by first) (map format-key-mapping) (s/join \newline))
         mm (->> info :macros (sort-by first) (map format-key-mapping) (s/join \newline))
         mj (->> info :mapped-to-joy (map format-key-mapping) (map #(str "// " %)) sort (s/join \newline))
         nm (->> info :not-mapped (map name) (map #(str "// " %)) sort (s/join \newline))
         ]
     (do (println "// Commands mapped to keys ----------------------------------------------------")
         (println mk)
         (println)
         (when macro-definitions
           (println "// Macros ---------------------------------------------------------------------")
           (println mm)
           (println))
         (when static-cmc
           (println "// Extra commands -------------------------------------------------------------")
           (println static-cmc)
           (println))
         (println "// Commands mapped to joystick buttons ----------------------------------------")
         (println mj)
         (println)
         (println "// Bindings not mapped or unknown ---------------------------------------------")
         (println nm))
     )
   )
  )
