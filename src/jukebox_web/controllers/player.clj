(ns jukebox-web.controllers.player
  (:use [jukebox-player.tags])
  (:require
    [jukebox-web.util.json :as json]
    [jukebox-player.core :as player]
    [jukebox-web.models.user :as user]
    [jukebox-web.models.playlist :as playlist]))

(defn play [request]
  (player/play!)
  (let [song (playlist/current-song)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
  (if (json/request? ((:headers request) "accept"))
    (json/response (merge (extract-tags song) {:progress progress :playing (player/playing?) :canSkip loggedin}))
    {:status 302 :headers {"Location" "/playlist"}})))

(defn pause [request]
  (player/pause!)
  (let [song (playlist/current-song)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
  (if (json/request? ((:headers request) "accept"))
    (json/response (merge (extract-tags song) {:progress progress :playing (player/playing?) :canSkip loggedin}))
    {:status 302 :headers {"Location" "/playlist"}})))

(defn skip [request]
  (when-let [current-user (-> request :session :current-user)]
    (player/skip!)
    (do (Thread/sleep 1000))
    (user/increment-skip-count! current-user))
  (let [song (playlist/current-song)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
  (if (json/request? ((:headers request) "accept"))
    (json/response (merge (extract-tags song) {:progress progress :playing (player/playing?) :canSkip loggedin}))
    {:status 302 :headers {"Location" "/playlist"}})))
