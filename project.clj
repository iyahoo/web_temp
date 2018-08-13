(defproject web_temp "0.1.0-SNAPSHOT"
  :description ""
  :url ""

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [wink "0.0.3"]]

  :main ^:skip-aot web_temp.core

  :profiles {:uberjar {:aot :all}})
