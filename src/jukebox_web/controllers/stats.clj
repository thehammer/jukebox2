(ns jukebox-web.controllers.stats
  (:require [jukebox-web.views.stats :as view]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.util.json :as json]))

(defn index [request]
  (view/index request (user/find-all) (library/most-played)))

(defn song-counts [request]
  (let [users (user/find-all)]
    (json/response (map #(vector (:login %) (user/count-songs %)) users))))
