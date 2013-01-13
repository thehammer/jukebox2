(ns jukebox-web.controllers.playlist
  (:use [jukebox-web.util.encoding :only [sha256]])
  (:require [jukebox-player.core :as player]
            [jukebox-web.util.json :as json]
            [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]))


(defn- build-playlist [user]
  (let [tracks (playlist/queued-songs)]
    (if-not (empty? tracks)
      (map #(playlist-track/metadata % user) tracks)
      {})))

(defn- current-user [request]
  (user/find-by-login (-> request :session :current-user)))

(defn index [request]
  (if (json/request? ((:headers request) "accept"))
      (json/response (build-playlist (current-user request)))
      (view/index request (playlist/current-song) (current-user request) (playlist/queued-songs))))

(defn current-track [request]
  (let [track (playlist/current-song)
        user (current-user request)
        html (view/current-track request track user (playlist/queued-songs))
        etag (sha256 html)]
    (if (json/request? ((:headers request) "accept"))
      (json/response (playlist-track/metadata track user))
      {:status 200 :headers {"E-Tag" etag} :body html})))

(defn delete [request]
  (let [user (current-user request)
        uuid (-> request :params :id)]
    (when (user/isRequester? (playlist/queued-song uuid) user)
      (playlist/delete-song! uuid))
    (if (json/request? ((:headers request) "accept"))
      (json/response (build-playlist user))
      {:status 302 :headers {"Location" "/playlist"}})))

(defn add-one [request]
  (let [user (current-user request)]
    (when (user/canAdd? user) (playlist/add-random-song!))
  (if (json/request? ((:headers request) "accept"))
    (json/response (build-playlist user))
    {:status 302 :headers {"Location" "/playlist"}})))

(defn add [request]
  (let [song (-> request :params :song)
        user (current-user request)]
    (when (user/canAdd? user) (playlist/add-song! song user))
    (if (json/request? ((:headers request) "accept"))
      (json/response (build-playlist user))
      {:status 302 :headers {"Location" "/playlist"}})))

(defn add-album [request]
  (let [album-directory (-> request :params :album-dir)
        user (current-user request)]
    (when (user/canAdd? user)
      (playlist/add-album! album-directory user))
    (if (json/request? ((:headers request) "accept"))
      (json/response (build-playlist user))
      {:status 302 :headers {"Location" "/playlist"}})))
