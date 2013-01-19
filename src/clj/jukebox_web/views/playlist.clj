(ns jukebox-web.views.playlist
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-player.core :as player]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.user :as user])
  (:use [hiccup core page]
        [jukebox-player.tags]))

(defn- build-avatar [current-song user]
  (let [current-song-owner (library/owner-md current-song)
        link (user/avatar-url user {:s 32})
        img-tag (if (= (:login user) current-song-owner) :img.current :img)]
      (vector img-tag {:src link :title (:login user)})))

(defn- display-enabled-users [current-track]
  (map (partial build-avatar current-track) (user/find-enabled)))

(defn- display-song [track user request]
  [:div.song.media-grid
    [:div#track
      [:div.album-cover {:data-thumbnail "large"
                         :data-title (:title track)
                         :data-artist (:artist track)
                         :data-album (:album track)}
       [:a {:href "#"} [:img.thumbnail {:src (:large (:artwork track))}]]]
     [:div.meta-data
        [:h1.title (:title track)]
        [:p.play-count "Play count: " (:play_count track)]
        [:p.skip-count "Skip count: " (:skip_count track)]
        [:p.owner "Owner: " (:owner track)]
        [:p.requester "Requester: " (:requester track)]
        [:p.artist (:artist track)]
        [:p.album (:album track)]]]
   [:div#player-controls.meta-data
      [:p.progress {:data-current (str (int (player/current-time))) :data-duration (str (:duration track))}
        [:span.remaining]]
      [:p.controls
       (if (player/paused?) [:a.btn.play {:href "/player/play" :data-remote "true"} "Play"])
       (if (player/playing?) [:a.btn.pause {:href "/player/pause" :data-remote "true"} "Pause"])
       (if (player/playing?) (when-not (nil? (-> request :session :current-user)) [:a.btn.skip {:href "/player/skip" :data-remote "true"} "Skip"]))]]])


(defn playlist [track user]
  [:div.meta-data
   [:h6.title (:title track)]
   [:p.artist (:artist track)]
   [:p.owner "Owner: " (:login (library/owner-md track))]
   [:p.requester "Requester: " (:requester track)]
   (when (:isRequester track)
     [:p
      [:a.delete-playlist-track {:href (str "/playlist/" (:playlist-id track) "/delete") :data-remote "true" :data-method "DELETE"} "Delete"]])
   ])

(defn display-current-track [request current-track user queued-songs]
  (html
    (display-song current-track user request)
    [:h3 "Playing Music From"]
    (display-enabled-users current-track)
    [:div.row
      [:h3 "Playlist"]
      [:ol#playlist.span12.clearfix
      (if-not (empty? queued-songs)
        (map #(vector :li (playlist % user)) queued-songs)
        [:li.random "Choosing random tracks"])]]))

(defn index [request current-track user queued-songs]
  (layout/main request (str (:title current-track) " - " (:artist current-track))
     [:div#current_track
       (display-current-track request current-track user queued-songs)]))
