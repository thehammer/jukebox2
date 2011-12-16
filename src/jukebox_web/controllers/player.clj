(ns jukebox-web.controllers.player
  (:use [jukebox-player.tags])
  (:require
    [jukebox-web.util.json :as json]
    [jukebox-player.core :as player]
    [jukebox-web.models.library :as library]
    [jukebox-web.models.user :as user]
    [jukebox-web.models.playlist :as playlist]))

(defn- respond-to [request]
  (let [track (playlist/current-song)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
  (if (json/request? ((:headers request) "accept"))
    (json/response (merge (extract-tags (:song track)) {:progress progress :playing (player/playing?) :canSkip loggedin :playCount (library/play-count (:song track)) :skipCount (library/skip-count (:song track))}))
    {:status 302 :headers {"Location" "/playlist"}})))

(defn play [request]
  (player/play!)
  (respond-to request))

(defn pause [request]
  (player/pause!)
  (respond-to request))

(defn skip [request]
  (when-let [current-user (-> request :session :current-user)]
    (player/skip!)
    (do (Thread/sleep 1000))
    (user/increment-skip-count! current-user))
    (library/increment-skip-count! (:song (playlist/current-song)))
  (respond-to request))
