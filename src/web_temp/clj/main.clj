(ns web_temp.clj.main
  (:require [web_temp.clj.core :as core])
  (:gen-class))

(defn parse-int [n]
  (if (number? n)
    n
    (Integer/parseInt n)))

(defn -main
  "I don't do a whole lot ... yet."
  [& {:as args}]
  (let [host (or (get args "host") (:host core/env) "0.0.0.0")
        port (parse-int
              (or (get args "port") (:port core/env) "3450"))]
    (prn "args:" args)
    (prn "host:" host)
    (prn "port:" port)
    (core/start-server {:host host :port port :join? true})))
