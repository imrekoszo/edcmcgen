(ns edcmcgen.rawbindings
  (:require [clojure.xml :as xml]
            [edcmcgen.utils :refer :all]))

(defn- seq-of-maps? [value]
  (and (sequential? value)
       (not (empty? value))
       (every? map? value)))

(defn- extract-content-and-attrs [m]
  [(:tag m)
   (merge {}
          (:attrs m)
          (cond (seq-of-maps? (:content m)) (into {} (map extract-content-and-attrs (:content m)))
                (nil? (:content m)) nil
                :else {:content (:content m)}))])

(defn- category [[_ {binding :Binding value :Value primary :Primary}]]
  (cond binding :axes
        value :settings
        primary :commands
        :else :misc))

(defn- flatten-settings [settings]
  (for [[n {v :Value}] settings] [n v]))

(defn- flatten-axes [axes]
  (for [[n {{key :Key device :Device} :Binding
            {iv :Value}               :Inverted
            {dv :Value}               :Deadzone}] axes]
    [n {:Device   device
        :Axis     key
        :Inverted iv
        :Deadzone dv}]))

(defn- flatten-commands [commands]
  (for [[n {{key :Key device :Device {modifier :Key} :Modifier} :Primary :as v}] commands]
    [n (-> (dissoc v :Primary :Secondary)
           (#(into-map (for [[k {v :Value}] %] [k v])))
           (assoc :Key key :Device device)
           (#(if modifier (assoc % :Modifier modifier) %)))]))

(defn- device [[_ {d :Device}]]
  (case d
    "Keyboard" :keyboard
    "{NoDevice}" :unbound
    :controller))

(def ^:private group-by-device (partial group-by device))

(defn- values-into-map [m]
  (into-map
    (for [[k v] m]
      [k (into-map v)])))

(defn- combine-keys [[suffix m]]
  (into-map (for [[k v] m] [(keyword (str (name k) \- (name suffix))) v])))

(defn- concat-raise [m ks]
  (->> (filter #((set ks) (first %)) m)
       (map combine-keys)
       (apply merge)
       (merge (apply dissoc (cons m ks)))))

(defn- remove-Device [mapping]
  (update mapping 1 #(dissoc % :Device)))

(defn- clean-mappings [mappings-by-device]
  (-> mappings-by-device
      (#(update-if-contains % :unbound (comp set (partial map first))))
      (#(update-if-contains % :keyboard (comp into-map (partial map remove-Device))))
      (#(update-if-contains % :controller into-map))))

(defn parse [s]
  (->> s
       xml/parse
       extract-content-and-attrs
       second
       (group-by category)
       (#(update % :misc into-map))
       (#(update % :settings (comp into-map
                                   flatten-settings)))
       (#(update % :axes (comp clean-mappings
                               group-by-device
                               flatten-axes)))
       (#(update % :commands (comp clean-mappings
                                   group-by-device
                                   flatten-commands)))
       (#(concat-raise % [:axes :commands]))))
