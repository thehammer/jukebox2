(ns jukebox-web.controllers.playlist
  (:require [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]))

(defn index [request]
  (view/index (-> request :session :current-user) (playlist/current-song) (playlist/queued-songs)))

(defn add-one [request]
  (playlist/add-random-song!)
  {:status 302 :headers {"Location" "/playlist"}})
