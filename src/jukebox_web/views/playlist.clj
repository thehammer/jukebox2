(ns jukebox-web.views.playlist
  (:use [hiccup core page-helpers]))

(defn index [current-song queued-songs]
  (html5
    [:head
     [:title "Playlist"]
     (include-css "/css/style.css")]
    [:body
     [:h3 "Current Song"]
     [:p current-song]
     [:h3 "Queued Songs"]
     [:ul (map #(vector :li %) queued-songs)]
     [:h3 "Operations"]
     [:ul
      [:li (link-to "/player/play" "Play")]
      [:li (link-to "/player/pause" "Pause")]
      [:li (link-to "/player/skip" "Skip")]
      [:li (link-to "/playlist/add-one" "Add random track")]]]))
