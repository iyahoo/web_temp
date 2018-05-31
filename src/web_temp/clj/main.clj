(ns web_temp.clj.main
  (:require [web_temp.clj.core :as core])
  (:gen-class))

(defn -main [& {:as args}]
  (core/start-server
   :host (get args "host") :port (get args "port") :join? true))
