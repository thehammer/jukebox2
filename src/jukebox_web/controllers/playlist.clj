(ns jukebox-web.controllers.playlist
  (:require [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]))

(defn index [request]
  (view/index (playlist/current-song)))
