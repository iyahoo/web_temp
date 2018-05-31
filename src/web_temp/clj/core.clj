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
            [hiccup.core :as hc :refer [html]]
            [bidi.ring :refer [make-handler]]))

(defonce server (atom nil))

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

(def app
  (-> handler
      wrap-restful-format
      wrap-anti-forgery
      wrap-session
      wrap-flash
      (wrap-resource "public")
      wrap-content-type))



(defn start-server [& {:keys [host port join?]
                       :or {host "localhost" port 3450 join? false}}]
  (let [port (cond (integer? port) port
                   (string? port) (Integer/parseInt port)
                   :else 3450)
        opts {:host host
              :port port
              :join? join?}]
    (prn :opts opts)
    (reset! server
            (run-jetty #'app opts))))



(defn stop-server []
  (.stop @server)
  (reset! server nil))



(defn restart-server []
  (when @server (stop-server))
  (start-server))
