(ns jukebox-web.views.stats
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.user :as user]
            [clojure.string :as string])
  (:use [hiccup core page-helpers form-helpers]))

(defn- nice-track-name [track]
  (string/replace track #"^music/" ""))

(defn index [request users most-played-tracks]
  (layout/main request "Stats"
    [:script {:src "https://www.google.com/jsapi"}]
    [:script {:src "/js/stats.js"}]
    [:div#song-count-chart]
    [:h3 "Most Played Tracks"]
    [:table
     [:tr [:th "Track"] [:th "Number of Plays"]]
     (map #(vector :tr [:td (nice-track-name (:track %))] [:td (:count %)]) most-played-tracks)]))
