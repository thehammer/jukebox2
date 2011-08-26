(ns jukebox-web.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [jukebox-web.playlist :as playlist]
            [jukebox-web.player :as player]))

(defroutes main-routes
  (GET "/" [] "<h1>Hello World</h1>")
  (GET "/playlist" [] playlist/index)
  (GET "/player/play" [] player/play)
  (GET "/player/pause" [] player/pause)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
