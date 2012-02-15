(ns jukebox-web.models.playlist-track
  (:use [jukebox-player.tags])
  (:import [java.util UUID])
  (:require [jukebox-player.core :as player]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.artwork :as artwork]
            [jukebox-web.models.user :as user]))

(defprotocol Print
  (metadata [this] {}))

(defrecord PlaylistTrack [song requester id artwork]
  Print
  (metadata [this]
    {}))

(defn new-playlist-track [song requester id & [artwork]]
  (if (nil? artwork)
    (let [tags (extract-tags song)
          image (artwork/album-cover (:album tags) (:artist tags))]
      (PlaylistTrack. song requester id image))
    (PlaylistTrack. song requester id (artwork/default-image))))

(defn metadata [track user]
  (let [tags (extract-tags (:song track))]
    (merge tags
           {:requester (:login (:requester track))
            :owner (library/owner (:song track))
            :playCount (library/play-count (:song track))
            :skipCount (library/skip-count (:song track))
            :progress (int (player/current-time))
            :playing (player/playing?)
            :artwork (:artwork track)
            :isRequester (user/isRequester? track user)
            :id (:id track)
            })))
