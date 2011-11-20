(ns jukebox-web.views.stats
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers form-helpers]))

(defn index [request users]
  (layout/main request "Stats"
    [:script {:src "https://www.google.com/jsapi"}]
    [:script {:src "/js/stats.js"}]
    [:div#song-count-chart]))
