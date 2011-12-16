(ns jukebox-web.models.playlist-track
  (:use [jukebox-player.tags])
  (:require [jukebox-web.models.library :as library]))

(defrecord PlaylistTrack [song requester])

(defn metadata [track]
  (merge (extract-tags (:song track))
         {:requester (:login (:requester track))
          :owner (library/owner (:song track))
          :playCount (library/play-count (:song track))
          :skipCount (library/skip-count (:song track))
          }))


