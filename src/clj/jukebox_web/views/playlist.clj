(ns jukebox-web.views.playlist
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-player.core :as player]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.user :as user])
  (:use [hiccup core page]
        [jukebox-player.tags]))

(defn- build-avatar [current-song user]
  (let [current-song-owner (library/owner current-song)
        link (user/avatar-url user {:s 32})
        img-tag (if (= (:login user) current-song-owner) :img.current :img)]
      (vector img-tag {:src link :title (:login user)})))

(defn- display-enabled-users [current-song]
  (map (partial build-avatar current-song) (user/find-enabled)))

(defn- display-song [track user request]
  (let [metadata (playlist-track/metadata track user)]
    [:div.song.media-grid
      [:div#track
        [:div.album-cover {:data-thumbnail "large" :data-title (:title metadata) :data-artist (:artist metadata) :data-album (:album metadata)}
         [:a {:href "#"} [:img.thumbnail {:src (:large (:artwork track))}]]]
       [:div.meta-data
          [:h1.title (:title metadata)]
          [:p.play-count "Play count: " (library/play-count (:song track))]
          [:p.skip-count "Skip count: " (library/skip-count (:song track))]
          [:p.owner "Owner: " (:owner metadata)]
          [:p.requester "Requester: " (:requester metadata)]
          [:p.artist (:artist metadata)]
          [:p.album (:album metadata)]]]
     [:div#player-controls.meta-data
        [:p.progress {:data-current (str (int (player/current-time))) :data-duration (str (:duration metadata))}
          [:span.remaining]]
        [:p.controls
         (if (player/paused?) [:a.btn.play {:href "/player/play" :data-remote "true"} "Play"])
         (if (player/playing?) [:a.btn.pause {:href "/player/pause" :data-remote "true"} "Pause"])
         (if (player/playing?) (when-not (nil? (-> request :session :current-user)) [:a.btn.skip {:href "/player/skip" :data-remote "true"} "Skip"]))]]]))


(defn playlist [track user]
  (let [metadata (playlist-track/metadata track user)]
    [:div.meta-data
     [:h6.title (:title metadata)]
     [:p.artist (:artist metadata)]
     [:p.owner "Owner: " (:owner metadata)]
     [:p.requester "Requester: " (:requester metadata)]
     (when (:isRequester metadata)
       [:p
        [:a.delete-playlist-track {:href (str "/playlist/" (:id metadata) "/delete") :data-remote "true" :data-method "DELETE"} "Delete"]])
     ]))

(defn current-track [request current-song user queued-songs]
  (html
    (display-song current-song user request)
    [:h3 "Playing Music From"]
    (display-enabled-users (:song current-song))
    [:div.row
      [:h3 "Playlist"]
      [:ol#playlist.span12.clearfix
      (if-not (empty? queued-songs)
        (map #(vector :li (playlist % user)) queued-songs)
        [:li.random "Choosing random tracks"])]]))

(defn index [request current-song user queued-songs]
  (let [tags (extract-tags (:song current-song))]
    (layout/main request (str (:title tags) " - " (:artist tags))
       [:div#current_track
         (current-track request current-song user queued-songs)])))
