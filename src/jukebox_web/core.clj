(ns jukebox-web.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.flash :as flash]
            [ring.adapter.jetty :as adapter]
            [jukebox-player.core :as player]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.controllers.hammertimes :as hammertimes-controller]
            [jukebox-web.controllers.library :as library-controller]
            [jukebox-web.controllers.playlist :as playlist-controller]
            [jukebox-web.controllers.player :as player-controller]
            [jukebox-web.controllers.users :as users-controller]))

(defroutes main-routes
  (GET "/" [] {:status 302 :headers {"Location" "/playlist"}})
  (GET "/playlist" [] playlist-controller/index)
  (GET "/playlist/current-track" [] playlist-controller/current-track)
  (GET "/playlist/add-one" [] playlist-controller/add-one)
  (POST "/playlist/add" [] playlist-controller/add)
  (GET ["/playlist/add/:song" :song #".*"] [] playlist-controller/add)
  (GET "/player/play" [] player-controller/play)
  (GET "/player/pause" [] player-controller/pause)
  (GET "/player/skip" [] player-controller/skip)
  (GET "/users" [] users-controller/index)
  (POST "/users/sign-out" [] users-controller/sign-out)
  (POST "/users/authenticate" [] users-controller/authenticate)
  (GET "/users/sign-up" [] users-controller/sign-up-form)
  (POST "/users/sign-up" [] users-controller/sign-up)
  (POST "/users/toggle-enabled" [] users-controller/toggle-enabled)
  (GET "/users/:id/edit" [] users-controller/edit)
  (POST "/users/:id/update" [] users-controller/update)
  (GET "/hammertimes" [] hammertimes-controller/index)
  (POST "/hammertimes" [] hammertimes-controller/create)
  (GET ["/hammertimes/new/:file" :file #".*"] [] hammertimes-controller/create-form)
  (POST "/hammertimes/play" [] hammertimes-controller/play)
  (POST "/hammertimes/:id/delete" [] hammertimes-controller/delete)
  (GET "/hammertimes/browse" [] hammertimes-controller/browse-root)
  (GET ["/hammertimes/browse/:path", :path #".*"] [] hammertimes-controller/browse)
  (POST "/library/upload" [] library-controller/upload)
  (GET "/library/browse" [] library-controller/browse-root)
  (GET ["/library/browse/:path", :path #".*"] [] library-controller/browse)
  (route/resources "/")
  (route/not-found "Page not found"))

(db/connect! "data/jukebox.fdb")

(player/start (playlist/playlist-seq))

(def app (handler/site (flash/wrap-flash main-routes)))

(defn -main [port & args]
  (adapter/run-jetty app {:port (read-string port)}))

