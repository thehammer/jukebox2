(ns jukebox-web.controllers.now-playing
  (:require [cheshire.core :as json]
            [jukebox-player.core :as player]
            [jukebox-web.models.playlist :as playlist]))

(defn current [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string {:player {:playing? (player/playing?)
                                         :current-time (player/current-time)}
                                :current-song (playlist/current-song)
                                :queued-songs (playlist/queued-songs)})})
