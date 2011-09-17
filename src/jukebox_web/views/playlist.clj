(ns jukebox-web.views.playlist
  (:use [hiccup core page-helpers]
        [jukebox-player.tags]
        [jukebox-web.views.layout :as layout]))

(defn display-song [song]
  (let [tags (extract-tags song)]
    [:div.song
      [:span.artist (:artist tags)]
      " - "
      [:span.title (:title tags)]
      " ("
      [:span.album (:album tags)]
      ")"]))

(defn index [current-song queued-songs]
  (layout/main "Playlist"
     [:h3 "Current Song"]
     [:p (display-song current-song)]
     [:h3 "Queued Songs"]
     [:ul (map #(vector :li (display-song %)) queued-songs)]
     [:h3 "Operations"]
     [:ul
      [:li (link-to "/player/play" "Play")]
      [:li (link-to "/player/pause" "Pause")]
      [:li (link-to "/player/skip" "Skip")]
      [:li (link-to "/playlist/add-one" "Add random track")]]
     [:h3 "Users"]
     [:ul
      [:li (link-to "/users/sign-up" "Sign Up")]
      [:li (link-to "/users/sign-in" "Sign In")]
      [:li (link-to "/users/sign-out" "Sign Out")]
  ]))
