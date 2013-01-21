(ns jukebox.now-playing
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [dommy.template :as template]
            [jukebox.core :as jukebox]
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
  (dom/replace-children! (dom/by-id "content")
                         (cover-flow state))
  (._init (js/ContentFlow. "playlist-flow")))

(add-watch jukebox/playlist-state
           :now-playing
           (fn [_ _ _ state] (render state)))
