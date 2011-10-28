(ns jukebox-web.controllers.playlist
  (:use [jukebox-web.util.encoding :only [sha256]])
  (:require [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.user :as user]))

(defn index [request]
  (view/index request (playlist/current-song) (playlist/queued-songs)))

(defn current-track [request]
  (let [html (view/current-track request (playlist/current-song) (playlist/queued-songs))
        etag ((:headers request) "if-none-match")
        sha (sha256 html)]
    (if (= sha etag)
      {:status 304 :headers {"E-Tag" sha} :body ""}
      {:status 200 :headers {"E-Tag" sha} :body html})))

(defn add-one [request]
  (playlist/add-random-song!)
  {:status 302 :headers {"Location" "/playlist"}})

(defn add [request]
  (let [song (-> request :params :song)]
    (playlist/add-song! song)
    {:status 302 :headers {"Location" "/playlist"}}))
