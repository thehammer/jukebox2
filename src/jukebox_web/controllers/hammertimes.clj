(ns jukebox-web.controllers.hammertimes
  (:require [jukebox-web.views.hammertimes :as view]))

(defn create [request]
  (view/create {}))
