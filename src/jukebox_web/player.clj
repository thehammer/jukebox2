(ns jukebox-web.player
  (:require [jukebox-web.playlist :as playlist]))


(defn- pause-track []
  (.pause (:track (playlist/current-song))))

(defn play [request]
  (playlist/set-current-song!)
  (.play (:track (playlist/current-song)))
  {:status 302 :headers {"Location" "/playlist"}})

(defn pause [request]
  (pause-track)
  {:status 302 :headers {"Location" "/playlist"}})

(defn skip [request]
  (pause-track)
  (playlist/skip-current-song!)
  {:status 302 :headers {"Location" "/player/play"}})
