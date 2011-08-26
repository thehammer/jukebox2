(ns jukebox-web.views.playlist
  (:use [hiccup core page-helpers]))

(defn index [current-song]
  (html5
    [:head
     [:title "Playlist"]
     (include-css "/css/style.css")]
    [:body
     [:h3 "Current Song"]
     [:p (:name current-song)]
     [:h3 "Operations"]
     [:ul
      [:li (link-to "/player/play" "Play")]
      [:li (link-to "/player/pause" "Pause")]
      [:li (link-to "/player/skip" "Skip")]]]))
