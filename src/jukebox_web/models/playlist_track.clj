(ns jukebox-web.models.playlist-track
  (:use [jukebox-player.tags])
  (:import [java.util UUID])
  (:require [jukebox-player.core :as player]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.artwork :as artwork]
            [jukebox-web.models.user :as user]))

(defprotocol Metadata
  (track-metadata [this user] {}))

(defrecord PlaylistTrack [song requester id artwork]
  Metadata
  (track-metadata [this user]
    (merge (extract-tags song)
       {
          :requester (:login requester)
          :owner (library/owner song)
          :playCount (library/play-count song)
          :skipCount (library/skip-count song)
          :progress (int (player/current-time))
          :playing (player/playing?)
          :artwork artwork
          :isRequester (user/isRequester? this user)
          :id id
       })))

(defn new-playlist-track [song requester id & [artwork]]
  (if (nil? artwork)
    (let [tags (extract-tags song)
          image (artwork/album-cover (:album tags) (:artist tags))]
      (PlaylistTrack. song requester id image))
    (PlaylistTrack. song requester id (artwork/default-images))))

(defn metadata [track user] (track-metadata track user))
