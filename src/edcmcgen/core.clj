(ns edcmcgen.core
  (:require [clojure.xml :as x]
            [clojure.string :as s]
            [clojure.edn :as edn]))

(defn primary? [m]
  (= :Primary (:tag m)))

(defn get-modifiers [binding-content]
  (->> binding-content
       (filter #(= :Modifier (:tag %)))
       (map #(get-in % [:attrs :Key]))))

(defn get-modifiers-and-key [binding-map]
  (->> [(get-modifiers (:content binding-map))
        (get-in binding-map [:attrs :Key])]
       flatten
       (s/join " ")))

(defn extract-primary-keybind [e]
  [(:tag e)
   (->> (:content e)
        (filter primary?)
        first
        get-modifiers-and-key
        )])

(defn second-not-empty? [l]
  ((complement empty?) (second l)))

(def replacements
  [["Key_Backspace" "BKSPC"]
   ["Key_Delete" "KBDEL"]
   ["Key_DownArrow" "KBDOWN"]
   ["Key_End" "KBEND"]
   ["Key_Enter" "ENT"]
   ["Key_Home" "KBHOME"]
   ["Key_Insert" "KBINS"]
   ["Key_LeftArrow" "KBLEFT"]
   ["Key_LeftBracket" "["]
   ["Key_LeftShift" "LSHF"]
   ["Key_PageDown" "KBPGDN"]
   ["Key_PageUp" "KBPGUP"]
   ["Key_RightArrow" "KBRIGHT"]
   ["Key_RightBracket" "]"]
   ["Key_Space" "SPC"]
   ["Key_Tab" "TAB"]
   ["Key_UpArrow" "KBUP"]
   ["Key_Period" "."]
   ["Key_Comma" ","]
   ["Key_Numpad_Add" "KP+"]
   ["Key_Numpad_Divide" "KP/"]
   ["Key_Numpad_Multiply" "KP*"]
   ["Key_Numpad_" "KP"]
   ["Key_" ""]])

(defn apply-replacements [s]
  (reduce #(apply (partial s/replace %1) %2)
          s
          replacements))

(defn lower-case-if-one-character [s]
  (if (= 1 (count s))
    (s/lower-case s)
    s))

(defn translate-key [[_ key]]
  [_
   (-> (apply-replacements key)
       lower-case-if-one-character)])

(defn joy-mapping? [mapping]
  (->> (second mapping)
       (re-matches #"Joy_.*")
       boolean))

(defn split-kb-joy [mappings]
  (let [{kb  false
         joy true} (group-by joy-mapping? mappings)]
    [kb joy]))

(defn split-second-not-empty [mappings]
  (let [{empty     false
         non-empty true} (group-by second-not-empty? mappings)]
    [non-empty empty]))

(defn get-config-content [s]
  (-> s
      x/parse
      :content))

(defn translate-macro [mac dict]
  (s/join " " (map #(get dict % %) mac)))

(defn value-map
  "Produce a map where for every [k v] of m, [k (f v)] is part of the new map"
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn get-translated-macros [macros keybindings]
  (when macros
    (value-map #(translate-macro % keybindings) (edn/read-string macros))))


(defn translate-binds [bindings macros]
  (->> (get-config-content bindings)
       (map extract-primary-keybind)
       split-second-not-empty
       (#(vector (->> (first %)
                      (map translate-key)
                      split-kb-joy)
                 (second %)))
       (#(hash-map :mapped-to-keys (->> % first first (into {}))
                   :mapped-to-joy (->> % first second (into {}))
                   :not-mapped (->> % second (map first))))
       (#(assoc % :macros (get-translated-macros macros (:mapped-to-keys %))))
       ))


