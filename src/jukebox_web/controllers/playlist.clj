(ns jukebox-web.controllers.playlist
  (:require [jukebox-web.views.playlist :as view]
            [jukebox-web.playlist :as playlist]))

(defn index [request]
  (view/index (playlist/current-song)))
