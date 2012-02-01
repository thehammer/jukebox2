(ns jukebox-web.models.playlist-track
  (:use [jukebox-player.tags])
  (:import [java.util UUID])
  (:require [jukebox-player.core :as player]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]))

(defrecord PlaylistTrack [song requester id])

(defn metadata [track user]
  (merge (extract-tags (:song track))
         {:requester (:login (:requester track))
          :owner (library/owner (:song track))
          :playCount (library/play-count (:song track))
          :skipCount (library/skip-count (:song track))
          :progress (int (player/current-time))
          :playing (player/playing?)
          :canSkip (user/canSkip? track user)
          }))


