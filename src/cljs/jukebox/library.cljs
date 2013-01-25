(ns jukebox.library
  (:require [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.ajax :as ajax]
            [jukebox.gutter-nav :as nav]))

(defn albums-link [artist-map]
  (let [artist (get artist-map "artist")]
    [:a.albums {:href "#" :data-artist (js/encodeURIComponent artist)} artist]))

(defn render-artists [artists]
  (template/node
    [:div#artists
      [:h3 "Artists"]
      [:ul.entries
        (map (fn [a] [:li (albums-link a)]) artists)]]))

(defn tracks-for-album-link [artist album-map]
  (let [album (get album-map "album")]
    [:a.tracks {:href "#" :data-artist artist :data-album (js/encodeURIComponent album)} album]))

(defn render-albums [artist albums]
  (template/node
    [:div#albums
      [:h3 artist]
      [:ul.entries
        (map (fn [album] [:li (tracks-for-album-link artist album)]) albums)]]))

(defn render-tracks [artist album tracks]
  (template/node
    [:div#tracks
      [:h3 (js/decodeURIComponent album) " by " (js/decodeURIComponent artist)]
      [:ul.entries
        (map (fn [track] [:li (get track "title")]) tracks)]]))

(defn show-artists [event]
  (ev/stop-propagation event)
  (nav/make-active! (.-parentNode (ev/target event)))
  (ajax/replace-remote "main" "/library/artists" render-artists attach-events))

(defn show-albums [event]
  (ev/stop-propagation event)
  (let [artist (-> (ev/target event) dom/attrs :data-artist)]
    (ajax/replace-remote "main"
                         (str "/library/artists/" artist)
                         (partial render-albums artist)
                         attach-events)))

(defn show-tracks [event]
  (ev/stop-propagation event)
  (let [artist (-> (ev/target event) dom/attrs :data-artist)
        album (-> (ev/target event) dom/attrs :data-album)]
    (ajax/replace-remote "main"
                         (str "/library/artists/" artist "/albums/" album)
                         (partial render-tracks artist album))))

(defn attach-events []
  (ev/listen! (dom/by-id "library-browse") :click show-artists)
  (ev/listen! (css/sel "#artists a.albums") :click show-albums)
  (ev/listen! (css/sel "#albums a.tracks") :click show-tracks))

(set! (.-onload js/window) attach-events)
