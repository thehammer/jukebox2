(ns jukebox-web.views.playlist
  (:use [hiccup core page-helpers]))

(defn index []
  (html5
    [:head
     [:title "Playlist"]
     (include-css "/css/style.css")]
    [:body
     [:h1 "Playlist"]
     [:ul
      [:li (link-to "/player/play" "Play")]
      [:li (link-to "/player/pause" "Pause")]
      ]]))
