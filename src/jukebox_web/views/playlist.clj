(ns jukebox-web.views.playlist
  (:require [jukebox-web.views.layout :as layout])
  (:use [hiccup core page-helpers]
        [hiccup core form-helpers]
        [jukebox-player.tags]))

(defn display-song [song]
  (let [tags (extract-tags song)]
    [:div.song
      [:span.artist (:artist tags)]
      " - "
      [:span.title (:title tags)]
      " ("
      [:span.album (:album tags)]
      ")"]))

(defn index [request current-song queued-songs]
  (layout/main request "Playlist"
     [:h3 "Current Song"]
     [:p (display-song current-song)]
     [:h3 "Queued Songs"]
     [:ul (map #(vector :li (display-song %)) queued-songs)]
     [:div.row
       [:div.span3
         [:h3 "Operations"]
         [:ul.unstyled
          [:li (link-to "/player/play" "Play")]
          [:li (link-to "/player/pause" "Pause")]
          (when-not (nil? (-> request :session :current-user)) [:li (link-to "/player/skip" "Skip")])]]
       [:div.span3
         [:h3 "Users"]
         [:ul.unstyled
          [:li (link-to "/users/sign-up" "Sign Up")]]]]
     [:ul#uploads ]))
