(ns jukebox-web.views.playlist
  (:require
    [jukebox-web.views.layout :as layout]
    [jukebox-player.core :as player]
    [jukebox-web.models.library :as library]
    [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers]
        [hiccup core form-helpers]
        [jukebox-player.tags]))

(defn- build-avatar [current-song user]
  (let [current-song-owner (library/owner current-song)
        link (:avatar user)
        img-tag (if (= (:login user) current-song-owner) :img.current :img)]
      (vector img-tag {:src (str link "?s=32")})))

(defn- display-enabled-users [current-song]
  (map
    (partial build-avatar current-song)
    (filter #(:enabled %) (user/find-all))))

(defn- display-song [song request]
  (let [tags (extract-tags song)]
    [:div.song.media-grid
      [:div.album-cover {:data-thumbnail "large" :data-artist (:artist tags) :data-album (:album tags)}]
      [:div.meta-data
        [:h1.title (:title tags)]
        [:p.artist (:artist tags)]
        [:p.album (:album tags)]
        [:p.duration (:duration tags)]
        [:p.controls
         (if (player/paused?) [:a.btn {:href "/player/play" :data-remote "true"} "Play"])
         (if (player/playing?) [:a.btn {:href "/player/pause" :data-remote "true"} "Pause"])
         (if (player/playing?) (when-not (nil? (-> request :session :current-user)) [:a.btn {:href "/player/skip" :data-remote "true"} "Skip"]))]]]))

(defn playlist [song]
  (let [tags (extract-tags song)]
    [:div.meta-data
     [:h6.title (:title tags)]
     [:p.artist (:artist tags)]]))

(defn current-track [request current-song queued-songs]
  (html
    (display-song current-song request)
    [:h3 "Playing Music From"]
    (display-enabled-users current-song)
    [:div.row
      [:h3 "Playlist"]
      (if-not (empty? queued-songs)
        [:ol#playlist.span6.clearfix (map #(vector :li (playlist %)) queued-songs)]
        [:p.random "Choosing random tracks"])]))

(defn index [request current-song queued-songs]
  (layout/main request "Playlist"
     [:script {:src "/js/playlist-refresh.js"}]
     [:input#current_track_etag {:type "hidden"}]
     [:div#current_track
       (current-track request current-song queued-songs)]))
