(defproject edcmcgen "0.2.0-SNAPSHOT"
  :description "A small utility to generate a CH Products cmc file from Elite: Dangerous bindings"
  :url "https://github.com/imrekoszo/edcmcgen"
  :license {:name "MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.2"]]
  :main ^:skip-aot edcmcgen.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
