(ns jukebox-web.controllers.stats
  (:require [jukebox-web.views.stats :as view]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.util.json :as json]))

(defn index [request]
  (view/index request (user/find-all) (library/most-played) (library/most-popular-artists)))

(defn song-counts [request]
  (let [users (user/find-all)
        users-with-counts (map #(vector (:login %) 0) users)
        sorted-users-with-counts (reverse (sort-by last users-with-counts))]
    (json/response sorted-users-with-counts)))
