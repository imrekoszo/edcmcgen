(ns edcmcgen.main
  (:require [edcmcgen.core :refer :all]
            [edcmcgen.options :refer :all]
            [edcmcgen.output :refer :all]
            [clojure.java.io :as io])
  (:gen-class))

; read config ✓
; read input ✓
; process input
;  transform xml into proper data ~
;  filter data into groups ~
;  translate e:d keys
;  do data manipulation
; format ✓
; add extra raw data ✓
; write output ✓

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
  {:elite-bindings    (io/input-stream bindings)
   :macro-definitions (when macros (slurp macros))
   :static-cmc        (when static (slurp static))})

(defn -main [& args]
  (let [options (-> args get-options check-options)
        {:keys [elite-bindings macro-definitions static-cmc]} (read-input options)
        info (translate-binds elite-bindings macro-definitions)]
    (print-cmc info static-cmc)))
