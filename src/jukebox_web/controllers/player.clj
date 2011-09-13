(ns jukebox-web.controllers.player
  (:require
    [jukebox-player.core :as player]
    [jukebox-web.models.playlist :as playlist]))

(defn play [request]
  (player/play)
  {:status 302 :headers {"Location" "/playlist"}})

(defn pause [request]
  (player/pause)
  {:status 302 :headers {"Location" "/playlist"}})

(defn skip [request]
  (player/skip)
  {:status 302 :headers {"Location" "/player/play"}})
