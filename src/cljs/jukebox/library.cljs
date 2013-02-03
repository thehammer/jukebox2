(ns jukebox.library
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.core :as jukebox]
            [jukebox.ajax :as ajax]
            [jukebox.gutter-nav :as nav]
            [jukebox.window :as window]))

(defn albums-link [artist-map]
  (let [artist (get artist-map "artist")]
    [:a.albums {:href "#" :data-artist artist} artist]))

(defn render-artists [artists]
  (template/node
    [:div#browse
      [:ul.breadcrumb
        [:li.active "Artists"]]
      [:ul.entries
        (map (fn [a] [:li (albums-link a)]) artists)]]))

(defn tracks-for-album-link [artist album-map]
  (let [album (get album-map "album")]
    [:a.tracks {:href "#" :data-artist artist :data-album album} album]))

(defn render-albums [artist albums]
  (template/node
    [:div#browse
      [:ul.breadcrumb
        [:li [:a.artists {:href "#"} "Artists"] [:span.divider "/"]]
        [:li.active artist]]
      [:ul.entries
        (map (fn [album] [:li (tracks-for-album-link artist album)]) albums)]]))

(defn add-track-link [track]
  (if @jukebox/current-user-state
    [:a.add-track {:href "#" :data-track-id (get track "id")} (get track "title")]
    (get track "title")))

(defn render-tracks [artist album tracks]
  (template/node
    [:div#browse
      [:ul.breadcrumb
        [:li [:a.artists {:href "#"} "Artists"] [:span.divider "/"]]
        [:li (albums-link {"artist" artist}) [:span.divider "/"]]
        [:li.active album]]
      [:ul.entries
        (map (fn [track] [:li (add-track-link track)]) tracks)]]))

(defn show-artists [event]
  (ev/prevent-default event)
  (ajax/replace-remote "content" "/library/artists" render-artists attach-events))

(defn show-albums [event]
  (ev/prevent-default event)
  (let [artist (-> (ev/target event) dom/attrs :data-artist)]
    (ajax/replace-remote "content"
                         (str "/library/artists/" (js/encodeURIComponent artist))
                         (partial render-albums artist)
                         attach-events)))

(defn show-tracks [event]
  (ev/prevent-default event)
  (let [artist (-> (ev/target event) dom/attrs :data-artist)
        album (-> (ev/target event) dom/attrs :data-album)]
    (ajax/replace-remote "content"
                         (str "/library/artists/" (js/encodeURIComponent artist) "/albums/" (js/encodeURIComponent album))
                         (partial render-tracks artist album)
                         attach-events)))

(defn add-track [event]
  (ev/prevent-default event)
  (let [track-id (-> (ev/target event) dom/attrs :data-track-id)]
    (xhr/send (str "/playlist/add-track/" track-id) dom/log "POST")))

(defn attach-events []
  (ev/listen! (css/sel "#browse a.add-track") :click add-track)
  (ev/listen! (css/sel "#browse a.artists") :click show-artists)
  (ev/listen! (css/sel "#browse a.albums") :click show-albums)
  (ev/listen! (css/sel "#browse a.tracks") :click show-tracks))

(nav/add-gutter-event "library-browse" show-artists)
