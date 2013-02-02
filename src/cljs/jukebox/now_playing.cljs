(ns jukebox.now-playing
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.core :as jukebox]
            [jukebox.gutter-nav :as nav]
            [jukebox.window :as window]
            [jukebox.player :as player]))

(defn cover-flow-item [track]
  [:img.item {:src (get track "xlarge_image")
              :title (str (get track "artist") " - " (get track "title"))}])

(defn cover-flow [now-playing]
  (template/node
    [:div.#playlist-flow.ContentFlow
      [:div.loadIndicator [:div.indicator]]
      [:div.flow
        (cover-flow-item (get now-playing "current-song"))
        (map cover-flow-item (get now-playing "queued-songs"))]
      [:div.globalCaption]
      [:div.scrollbar
        [:div.slider]
        [:div.position]]]))

(defn current-track [now-playing]
  (template/node
    [:img {:src (get-in now-playing ["current-song" "large_image"])}]))

(defn render [state]
  (dom/replace-children! (dom/by-id "content") (cover-flow state))
  (._init (js/ContentFlow. "playlist-flow")))

(defn show-now-playing [event]
  (render @jukebox/playlist-state))

(add-watch jukebox/playlist-state :now-playing (fn [_ _ _ state] (render state)))

(nav/add-gutter-event "now-playing" show-now-playing)
