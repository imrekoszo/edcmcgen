(ns edcmcgen.core
  (:require [clojure.string :as s]
            [clojure.edn :as edn]
            [edcmcgen.rawbindings :as raw]
            [edcmcgen.utils :refer :all]
            [edcmcgen.dictionary :refer :all]))

(defn apply-replacements [s]
  (reduce #(apply (partial s/replace %1) %2)
          s
          replacements-in-order-of-application))

(defn lower-case-if-one-character [s]
  (if (= 1 (count s))
    (s/lower-case s)
    s))

(defn translate-macro [mac dict]
  (s/join " " (map #(get dict % %) mac)))

(defn value-map
  "Produce a map where for every [k v] of m, [k (f v)] is part of the new map"
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn get-translated-macros [macros keybindings]
  (when macros
    (value-map #(translate-macro % keybindings) (edn/read-string macros))))

(defn- translate-keyboard-command [{key :Key modifier :Modifier}]
  (->> (if modifier
         (str modifier " " key)
         key)
       apply-replacements
       lower-case-if-one-character))

(defn translate-binds [bindings macros]
  (->> (raw/parse bindings)
       (#(update % :keyboard-commands
                 (partial value-map translate-keyboard-command)))

       (#(assoc % :macros (get-translated-macros macros (:keyboard-commands %))))))
