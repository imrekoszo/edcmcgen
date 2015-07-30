(ns edcmcgen.cli
  (:require [clojure.string :as s])
  (:import [java.io.File]))

(defn existing-file? [s]
  (let [f (clojure.java.io/as-file s)]
    (and (.exists f)
         (not (.isDirectory f)))))

(def cli-options
  [["-h" "--help"]
   [nil "--macro-file PATH" "Path to an edn file containing macro definitions"
    :validate [existing-file? "Must be an existing file"]]
   [nil "--extra-commands-file PATH" "Path to a cmc file whose contents are to be included in the output"
    :validate [existing-file? "Must be an existing file"]]
   ])

(defn usage [options-summary]
  (->> ["This program is a helper to create CH Products mappings from Elite: Dangerous bindings"
        ""
        "Usage lein run -- [options] <path-to-binding-file>"
        ""
        "Options:"
        options-summary
        ""]
       (s/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))