(ns jukebox-web.controllers.playlist
  (:use [jukebox-web.util.encoding :only [sha256]]
        [jukebox-player.tags])
  (:require [jukebox-player.core :as player]
            [jukebox-web.util.json :as json]
            [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.user :as user]))

(defn index [request]
    (if (json/request? ((:headers request) "accept"))
      (json/response {})
  (view/index request (playlist/current-song) (playlist/queued-songs))))

(defn current-track [request]
  (let [song (playlist/current-song)
        html (view/current-track request song (playlist/queued-songs))
        etag (sha256 html)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
    (if (json/request? ((:headers request) "accept"))
      (json/response (merge (extract-tags song) {:progress progress :playing (player/playing?) :canSkip loggedin}))
      {:status 200 :headers {"E-Tag" etag "X-Progress" (str progress)} :body html})))

(defn add-one [request]
  (playlist/add-random-song!)
  {:status 302 :headers {"Location" "/playlist"}})

(defn add [request]
  (let [song (-> request :params :song)]
    (playlist/add-song! song)
    {:status 302 :headers {"Location" "/playlist"}}))
