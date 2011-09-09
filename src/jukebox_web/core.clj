(ns jukebox-web.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
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
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
