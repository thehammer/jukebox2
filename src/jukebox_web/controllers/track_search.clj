(ns jukebox-web.controllers.track-search
  (:use [jukebox-player.tags])
  (:require [jukebox-web.util.json :as json]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.track-search :as track-search]
            [jukebox-web.models.user :as user]))

(defn- parse-results [song]
  (merge (extract-tags song) {:playCount (library/play-count song)}))

(defn index [request]
  (let [query (-> request :params :q)
        results (map parse-results (track-search/execute query))]
    (json/response results)))
