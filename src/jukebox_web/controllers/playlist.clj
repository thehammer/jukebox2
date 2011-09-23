(ns jukebox-web.controllers.playlist
  (:require [jukebox-web.views.playlist :as view]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.user :as user]))

(defn index [request]
  (let [login (-> request :session :current-user)
        current-user (user/find-by-login login)]
    (view/index current-user (playlist/current-song) (playlist/queued-songs))))

(defn add-one [request]
  (playlist/add-random-song!)
  {:status 302 :headers {"Location" "/playlist"}})

(defn add [request]
  (let [song (-> request :params :song)]
    (playlist/add-song! song)))
