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

(defn show-artists [event]
  (ev/stop-propagation event)
  (nav/make-active! (.-parentNode (ev/target event)))
  (ajax/replace-remote "main" "/library/artists" render-artists attach-events))

(defn show-albums [event]
  (ev/stop-propagation event)
  (ajax/replace-remote "main"
                       (str "/library/artists/" (-> (ev/target event) dom/attrs :data-artist))
                       dom/log))

(defn attach-events []
  (ev/listen! (dom/by-id "library-browse") :click show-artists)
  (ev/listen! (css/sel "#artists a.albums") :click show-albums))

(set! (.-onload js/window) attach-events)
