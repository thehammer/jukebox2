(ns jukebox-web.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [jukebox-player.core :as player]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.controllers.playlist :as playlist-controller]
            [jukebox-web.controllers.player :as player-controller]
            [jukebox-web.controllers.users :as users-controller]))

(defroutes main-routes
  (GET "/" [] {:status 302 :headers {"Location" "/playlist"}})
  (GET "/playlist" [] playlist-controller/index)
  (GET "/playlist/add-one" [] playlist-controller/add-one)
  (GET "/player/play" [] player-controller/play)
  (GET "/player/pause" [] player-controller/pause)
  (GET "/player/skip" [] player-controller/skip)
  (GET "/users/sign-in" [] users-controller/sign-in)
  (POST "/users/authenticate" [] users-controller/authenticate)
  (GET "/users/sign-up" [] users-controller/sign-up-form)
  (POST "/users/sign-up" [] users-controller/sign-up)
  (route/resources "/")
  (route/not-found "Page not found"))

(player/start (playlist/playlist-seq))

(defn with-connection [handler]
  (fn [request]
    (let [connection (db/open-db "data/jukebox.fdb")
          response (binding [db/*db* connection] (handler request))]
      (db/close-db connection)
      response)))

(def app
  (-> (handler/site main-routes)
      (with-connection)))
