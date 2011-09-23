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

(defn index [current-user current-song queued-songs]
  (let [{:keys [login skip-count]} current-user]
    (layout/main "Playlist"
       [:h3 "Current Song"]
       [:p (display-song current-song)]
       [:h3 "Queued Songs"]
       [:ul (map #(vector :li (display-song %)) queued-songs)]
       [:h3 "Operations"]
       [:ul
        [:li (link-to "/player/play" "Play")]
        [:li (link-to "/player/pause" "Pause")]
        [:li (link-to "/player/skip" "Skip")]]
       [:h3 "Users"]
       [:ul
        [:li (link-to "/users" "Users")]
        (when-not (nil? current-user) [:li (format "logged in as: %s (skips: %s)" login skip-count)])
        (when-not (nil? current-user) [:li (form-to [:post "/users/sign-out"] (submit-button "Sign Out"))])
        [:li (link-to "/users/sign-up" "Sign Up")]
        [:li (link-to "/users/sign-in" "Sign In")]]
       [:h3 "Library"]
       [:ul
         [:li (link-to "/playlist/add-one" "Add random track")]
         [:li (link-to "/library/browse" "Add track from library")]
         [:li [:form [:div#uploader]]]]
       [:h3 "Hammertimes"]
       [:ul
         [:li (link-to "/hammertimes" "List")]
         [:li (link-to "/hammertime" "Create")]])))
