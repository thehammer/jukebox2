(ns jukebox-web.playlist
  (:require [jukebox-web.views.playlist :as view]))

(defn index [request]
  (view/index))
