(ns edcmcgen.core
  (:require [clojure.string :as s]
            [clojure.edn :as edn]
            [edcmcgen.rawbindings :as raw]
            [edcmcgen.utils :refer :all]
            [edcmcgen.dictionary :refer :all]))

(defn- apply-replacements [s]
  (reduce #(apply (partial s/replace %1) %2)
          s
          replacements-in-order-of-application))

(defn- lower-case-if-one-character [s]
  (if (= 1 (count s))
    (s/lower-case s)
    s))

(defn- translate-keyboard-command [{key :Key modifier :Modifier}]
  (->> (if modifier
         (str modifier " " key)
         key)
       apply-replacements
       lower-case-if-one-character))

(defn- translate-all-keyboard-commands [m]
  (update m :keyboard-commands #(map-all-values % translate-keyboard-command)))

(defn- translate-macro [mac dict]
  (s/join " " (map #(get dict % %) mac)))

(defn- get-translated-macros [macro-string keybindings]
  (when-let [macros (edn/read-string macro-string)]
    (map-all-values macros translate-macro keybindings)))

(defn- add-macros [m macros-string]
  (assoc m :macros (get-translated-macros macros-string (:keyboard-commands m))))

(defn translate-binds [bindings macros]
  (-> (raw/parse bindings)
      translate-all-keyboard-commands
      (add-macros macros)))
