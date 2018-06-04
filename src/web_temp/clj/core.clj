;; -*- coding: utf-8 -*-
(ns web_temp.clj.core
  (:require [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.anti-forgery :as anti-forgery :refer [wrap-anti-forgery]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as res]
            [ring.util.request :refer [body-string]]
            [ring.component.jetty :refer [jetty-server]]
            [com.stuartsierra.component :as component]
            [hiccup.core :as hc :refer [html]]
            [bidi.ring :refer [make-handler]]
            [environ.core :as environ]))

(defn anti-forgery-script-tag []
  ;; cljsのjs/antiForgeryTokenにトークンを束縛する。
  ;; *anti-forgery-token*は動的束縛で、ring.middleware.sessionの
  ;; 各セッション毎に生成される。
  ;; そのため、この関数はリクエスト毎に呼び出される必要がある。
  ;; また、このタグを埋め込むハンドラではセッションをresに設定しなければいけない。
  (hc/html
   [:script {:type "text/javascript"}
    (str "var antiForgeryToken = "
         (pr-str anti-forgery/*anti-forgery-token*))]))

(defn make-response [chtml]
  (-> chtml
      res/response
      (res/header "Content-Type" "text/html")
      (res/charset "utf-8")))

(defn struct-to-htmlres [htmls]
  (-> htmls
      hc/html
      make-response))

(defn struct-for-jsload [& {:keys [jspath csspath]}]
  [:html
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1, minimum-scale=1"}]
    [:link {:rel "stylesheet" :href csspath :type "text/css"}]]
   [:body
    [:div#app]
    (anti-forgery-script-tag)
    [:script {:src jspath :type "text/javascript"}]]])

(defn notfound-page []
  [:html
   [:head
    [:title "Not Found"]]
   [:body
    [:a {:href "/"} "home"]]])

(defn notfound-handler [req]
  (prn :notfound (:uri req))
  (-> (notfound-page)
      hc/html
      make-response))

(defn index-struct []
  (struct-for-jsload :csspath "/css/style.css" :jspath "js/compiled/web_temp.js"))

(defn index [req]
  (let [sess (:session req)]
    (-> (index-struct)
        struct-to-htmlres
        (assoc :session sess))))

(def handler
  (make-handler ["/"
                 [["" index]
                  ["index.html" index]
                  [true notfound-handler]]]))

(def wrapped-app
  (-> handler
      wrap-restful-format
      wrap-anti-forgery
      wrap-session
      wrap-flash
      (wrap-resource "public")
      wrap-content-type))

(defn http-server [opt]
  (jetty-server opt))

(def initial-env
  {:app-port 3450
   :absolute-uri ""})

(defonce env
  (merge initial-env environ/env))

(def default-server-opt
  {:app {:handler wrapped-app}
   :port (:app-port env)})

(defn create-system [server-opt]
  (component/system-map
   :server (http-server (merge default-server-opt server-opt))))

(def main-system
  (atom (create-system {})))

(defn start-server
  ([server-opt]
   (reset! main-system (create-system server-opt))
   (start-server))
  ([]
   (swap! main-system component/start)))

(defn stop-server []
  (swap! main-system component/stop)
  (prn "web_temp.clj.core system stop"))
