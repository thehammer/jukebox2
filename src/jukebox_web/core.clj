(ns jukebox-web.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [jukebox-web.playlist :as playlist]
            [jukebox-web.player :as player]))

(defroutes main-routes
  (GET "/" [] {:status 302 :headers {"Location" "/playlist"}})
  (GET "/playlist" [] playlist/index)
  (GET "/player/play" [] player/play)
  (GET "/player/pause" [] player/pause)
  (GET "/player/skip" [] player/skip)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
