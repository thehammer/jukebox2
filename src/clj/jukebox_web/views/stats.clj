(ns jukebox-web.views.stats
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.user :as user]
            [clojure.string :as string])
  (:use [hiccup core page form]))

(defn- nice-track-name [track]
  (string/replace track #"^music/" ""))

(defn index [request users most-played-tracks most-tracks-per-artist]
  (layout/main request "Stats"
    [:script {:src "https://www.google.com/jsapi"}]
    [:script {:src "/js/stats.js"}]
    [:div#song-count-chart]
    [:h3 "Most Played Tracks"]
    [:table
     [:tr [:th "Track"] [:th "Number of Plays"]]
     (map #(vector :tr [:td (nice-track-name (:track %))] [:td (:count %)]) most-played-tracks)]
    [:h3 "Most Popular Artists"]
    [:table
     [:tr [:th "Artist"] [:th "Number of Tracks"]]
     (map #(vector :tr [:td (first %)] [:td (last %)]) most-tracks-per-artist)]))
