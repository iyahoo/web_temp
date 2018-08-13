(ns web_temp.core
  (:gen-class)
  (:require [wink.server :as server]
            [wink.tayen.flat :as f]
            [wink.tayen.styleutil :as u :refer [>> stls defstyle]]
            [wink.tayen.npek :as npek]
            [clojure.string :as s]))


;; constant





;;

(defstyle style-app
  (-> (f/class :app)
      (u/flex-column)))




(def styles
  (let [xs @stls]
    (doseq [x xs]
      (npek/assert x))
    (->> xs
         (map f/render-to-string)
         (s/join ""))))




;; server core

(defn make-style-renderer [config]
  (fn [req]
    (s/join "\n" [styles])))

(def server (server/single-page-application
             {:base :dev
              :gear {:html {:style make-style-renderer}}
              :config {:html {:css ["wink/css/ress/1.2.2/ress.min.css"]}}}))

(defn start []
  (.start server))

(defn stop []
  (.stop server))

(defn -main []
  (start))


(defn reload []
  (stop)
  (reset! stls [])
  (load-file "src/web_temp/core.clj")
  (start))
