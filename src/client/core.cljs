(ns client.core
  (:require [wink.leflame :as le]
            [wink.util :refer [>-]]))



(enable-console-print!)



(defn app []
  [:div.hello "hello"])



(le/mount-on app "app-root")
