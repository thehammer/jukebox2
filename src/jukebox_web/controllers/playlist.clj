(ns jukebox-web.controllers.playlist
  (:use [jukebox-web.util.encoding :only [sha256]])
  (:require [jukebox-player.core :as player]
            [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.user :as user]))

(defn index [request]
  (view/index request (playlist/current-song) (playlist/queued-songs)))

(defn current-track [request]
  (let [html (view/current-track request (playlist/current-song) (playlist/queued-songs))
        etag (sha256 html)
        previous-etag ((:headers request) "if-none-match")
        progress (str (int (player/current-time)))]
    (if (= etag previous-etag)
      {:status 304 :headers {"E-Tag" etag "X-Progress" progress} :body ""}
      {:status 200 :headers {"E-Tag" etag "X-Progress" progress} :body html})))

(defn add-one [request]
  (playlist/add-random-song!)
  {:status 302 :headers {"Location" "/playlist"}})

(defn add [request]
  (let [song (-> request :params :song)]
    (playlist/add-song! song)
    {:status 302 :headers {"Location" "/playlist"}}))
