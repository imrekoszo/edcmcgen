(ns edcmcgen.main-tests
  (:require [edcmcgen.main :refer [process]]
            [clojure.test :refer :all])
  (:import (java.io ByteArrayInputStream)))


(def binds-content
  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<Root PresetName=\"Custom\">
	<CamTranslateUp>
		<Primary Device=\"Keyboard\" Key=\"Key_D\" />
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</CamTranslateUp>
	<CamTranslateDown>
		<Primary Device=\"Keyboard\" Key=\"Key_M\">
			<Modifier Device=\"Keyboard\" Key=\"Key_LeftShift\" />
		</Primary>
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</CamTranslateDown>
	<PrimaryFire>
		<Primary Device=\"068EC010\" Key=\"Joy_1\" />
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</PrimaryFire>
	<BackwardKey>
		<Primary Device=\"{NoDevice}\" Key=\"\" />
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</BackwardKey>
	<IncreaseEnginesPower>
		<Primary Device=\"Keyboard\" Key=\"Key_2\" />
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</IncreaseEnginesPower>
	<ResetPowerDistribution>
		<Primary Device=\"Keyboard\" Key=\"Key_4\" />
		<Secondary Device=\"{NoDevice}\" Key=\"\" />
	</ResetPowerDistribution>
</Root>")


(def simple-output
  "// Commands bound to keys ------------------------------------------------------
CamTranslateDown                    LSHF m
CamTranslateUp                      d
IncreaseEnginesPower                2
ResetPowerDistribution              4

// Commands bound to controller or mouse buttons -------------------------------
// PrimaryFire                         {:Key \"Joy_1\", :Device \"068EC010\"}

// Unbound commands ------------------------------------------------------------
// BackwardKey

")

(def static-content
  "// Hidden commands
hShowFPS                            CTL f")


(def macros-content
  "{:mPowerPresetEngines1 [:ResetPowerDistribution CHARDLY :IncreaseEnginesPower CHARDLY :IncreaseEnginesPower]}")

(def output-with-macros-and-static-content
  "// Commands bound to keys ------------------------------------------------------
CamTranslateDown                    LSHF m
CamTranslateUp                      d
IncreaseEnginesPower                2
ResetPowerDistribution              4

// Macros ----------------------------------------------------------------------
mPowerPresetEngines1                4 CHARDLY 2 CHARDLY 2

// Static content --------------------------------------------------------------
// Hidden commands
hShowFPS                            CTL f

// Commands bound to controller or mouse buttons -------------------------------
// PrimaryFire                         {:Key \"Joy_1\", :Device \"068EC010\"}

// Unbound commands ------------------------------------------------------------
// BackwardKey

")

(defn string-stream [s]
  (ByteArrayInputStream. (.getBytes s)))

(defn do-process [m]
  (with-out-str (process (update m :elite-bindings string-stream))))

(deftest can-process-bindings
  (is (= simple-output
         (do-process {:elite-bindings binds-content}))))

(deftest can-process-macros-and-static-content
  (is (= output-with-macros-and-static-content
         (do-process {:elite-bindings binds-content
                      :macro-definitions macros-content
                      :static-cmc static-content}))))
