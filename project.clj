(defproject web_temp "0.1.0-SNAPSHOT"
  :description ""
  :url ""

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.bhauman/rebel-readline "0.1.4"]
                 [wink "0.0.3"]
                 ;; https://github.com/ptaoussanis/timbre/issues/263
                 [org.clojure/tools.reader "1.2.2"]]
  :aliases {"rebl"  ["trampoline" "run" "-m" "rebel-readline.main"]}

  :main ^:skip-aot web_temp.core

  :clean-targets^{:protect false} [:target-path "resources/public/out"]

  :profiles {:uberjar {:aot :all}})
