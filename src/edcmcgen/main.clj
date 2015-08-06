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

; read config ✓
; read input ✓
; process input
;  transform xml into proper data
;  filter data into groups
;  translate e:d keys
;  do data manipulation
; format ✓
; add extra raw data ✓
; write output ✓

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
                   macros   :macro-definitions-path
                   static   :static-cmc-content-path}]
  {:elite-bindings    (io/input-stream bindings)
   :macro-definitions (when macros (slurp macros))
   :static-cmc        (when static (slurp static))})

(defn format-bindings [bindings]
  (->> bindings
       (sort-by first)
       (map format-key-mapping)
       (s/join \newline)))

(defn format-commented [content f]
  (->> content
       (map f)
       (map #(str "// " %))
       sort
       (s/join \newline)))

(defn right-pad-with-dash [s width]
  (let [dash-count (- width (count s))
        dashes (take dash-count (repeat \-))]
    (apply str (cons s dashes))))

(defn print-section [name content]
  (let [header (right-pad-with-dash (str "// " name " ") 80)]
    (do (println header)
        (println content)
        (println))))

(defn print-output [{:keys [mapped-to-keys
                            macros
                            mapped-to-joy
                            not-mapped]}
                    static-cmc]
  (do (print-section "Commands mapped to keys" (format-bindings mapped-to-keys))
      (when macros
        (print-section "Macros" (format-bindings macros)))
      (when static-cmc
        (print-section "Static content" static-cmc))
      (print-section "Commands mapped to joystick buttons"
                     (format-commented mapped-to-joy format-key-mapping))
      (print-section "Bindings not mapped or unknown"
                     (format-commented not-mapped name))))

(defn -main [& args]
  (let [options (get-valid-options args)
        {:keys [elite-bindings
                macro-definitions
                static-cmc]} (read-input options)
        info (translate-binds elite-bindings macro-definitions)]
    (print-output info static-cmc)))
