(ns jukebox-web.views.playlist
  (:require [jukebox-web.views.layout :as layout])
  (:use [hiccup core page-helpers]
        [hiccup core form-helpers]
        [jukebox-player.tags]))

(defn display-song [song]
  (let [tags (extract-tags song)]
    [:div.song.media-grid
      [:div.album-cover {:data-artist (:artist tags) :data-album (:album tags)}]
      [:h1.title (:title tags)]
      [:p.artist (:artist tags)]
      [:p.album (:album tags)]]))

(defn index [request current-song queued-songs]
  (layout/main request "Playlist"
     (display-song current-song)
     [:h3 "Playlist"]
     [:ol#playlist (map #(vector :li (display-song %)) queued-songs)]
     [:div.row
       [:div.span3
         [:h3 "Operations"]
         [:ul.unstyled
          [:li (link-to "/player/play" "Play")]
          [:li (link-to "/player/pause" "Pause")]
          (when-not (nil? (-> request :session :current-user)) [:li (link-to "/player/skip" "Skip")])]]]))
