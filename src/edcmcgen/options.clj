(ns edcmcgen.options
  (:require [clojure.string :as s]
            [clojure.tools.cli :as cli])
  (:import [java.io.File]))

(defn- existing-file? [s]
  (let [f (clojure.java.io/as-file s)]
    (and (.exists f)
         (not (.isDirectory f)))))

(def ^:private cli-options
  [["-h" "--help"]
   [nil "--macro-file PATH" "Path to an edn file containing macro definitions"
    :validate [existing-file? "Must be an existing file"]]
   [nil "--extra-commands-file PATH" "Path to a cmc file whose contents are to be included in the output"
    :validate [existing-file? "Must be an existing file"]]
   ])

(defn- usage [options-summary]
  (->> ["This program is a helper to create CH Products mappings from Elite: Dangerous bindings"
        ""
        "Usage lein run -- [options] <path-to-binding-file>"
        ""
        "Options:"
        options-summary
        ""]
       (s/join \newline)))

(defn- error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))

(defn get-options [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        elite-bindings-path (first arguments)]
    (cond
      (:help options) {:help (usage summary)}
      (not= 1 (count arguments)) {:error (usage summary)}
      (not (existing-file? elite-bindings-path)) {:error (error-msg ["Invalid binding file path"])}
      errors {:error (error-msg errors)}
      :else {:elite-bindings-path     elite-bindings-path
             :macro-definitions-path  (:macro-file options)
             :static-cmc-content-path (:extra-commands-file options)})))
