(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [edcmcgen.options :refer :all]
            [edcmcgen.output :refer :all]
            [clojure.java.io :as io])
  (:gen-class))

(defn- exit [status msg]
  (println msg)
  (System/exit status))

(defn- check-options [options]
  (cond (contains? options :help) (exit 0 (:help options))
        (contains? options :error) (exit 1 (:error options))
        :else options))

(defn read-input [{bindings :elite-bindings-path
                   macros   :macro-definitions-path
                   static   :static-cmc-content-path}]
  {:elite-bindings    bindings
   :macro-definitions (when macros (slurp macros))
   :static-cmc        (when static (slurp static))})

(defn process [{:keys [elite-bindings macro-definitions static-cmc]}]
  (-> (translate-binds elite-bindings macro-definitions)
      (print-cmc static-cmc)))

(defn -main [& args]
  (-> args
      get-options
      check-options
      read-input
      process))
