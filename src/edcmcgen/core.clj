(ns edcmcgen.core
  (:gen-class)
  (:require [clojure.xml :as x]
            [clojure.string :as s]))

(defn primary? [m]
  (= :Primary (:tag m)))

(defn extract-primary-keybind [e]
  [(:tag e)
   (->> (:content e)
        (filter primary?)
        first
        :attrs
        :Key
        #_(get-in % [:attrs :Key])
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
  (let [{kb false
         joy true} (group-by joy-mapping? mappings)]
    [kb joy]))

(defn split-second-not-empty [mappings]
  (let [{empty     false
         non-empty true} (group-by second-not-empty? mappings)]
    [non-empty empty]))

(defn format-key-mapping [[command key]]
  (format "%1$-35s %2$s" (name command) key))

(defn translate-binds [s]
  (->> (x/parse s)
       :content
       (map extract-primary-keybind)
       split-second-not-empty
       (#(vector (->> (first %)
                      (map translate-key)
                      split-kb-joy)
                 (second %)))
       (#(hash-map :mapped-to-keys (->> % first first)
                   :mapped-to-joy  (-> % first second)
                   :not-mapped     (->> % second
                                        (map first)
                                        )))
       ))