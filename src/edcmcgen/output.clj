(ns edcmcgen.output
  (:require [clojure.string :as s]))

(defn- right-pad-with-dash [s width]
  (let [dash-count (- width (count s))
        dashes (take dash-count (repeat \-))]
    (apply str (cons s dashes))))

(defn- print-section [name content]
  (let [header (right-pad-with-dash (str "// " name " ") 80)]
    (do (println header)
        (println content)
        (println))))

(defn- format-key-mapping [[command key]]
  (format "%1$-35s %2$s" (name command) key))

(defn- format-bindings [bindings]
  (->> bindings
       (sort-by first)
       (map format-key-mapping)
       (s/join \newline)))

(defn- format-commented [content f]
  (->> content
       (map f)
       (map #(str "// " %))
       sort
       (s/join \newline)))

(defn print-cmc [{:keys [mapped-to-keys
                         macros
                         mapped-to-joy
                         not-mapped]}
                 static-content]
  (do (print-section "Commands mapped to keys" (format-bindings mapped-to-keys))
      (when macros
        (print-section "Macros" (format-bindings macros)))
      (when static-content
        (print-section "Static content" static-content))
      (print-section "Commands mapped to joystick buttons"
                     (format-commented mapped-to-joy format-key-mapping))
      (print-section "Bindings not mapped or unknown"
                     (format-commented not-mapped name))))
