(ns jukebox.now-playing
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [dommy.template :as template]
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

(defn render-now-playing [response]
  (let [data (js->clj (.getResponseJson (.-target response)))]
    (dom/log data)
    (dom/log (cover-flow data))
    (dom/replace-children! (dom/by-id "current-track")
                           (current-track data))
    (dom/log "rendering player controls")
    (player/render data)
    (dom/replace-children! (dom/by-id "content")
                           (cover-flow data))
    (._init (js/ContentFlow. "playlist-flow"))
    (dom/log (current-track data))))

(defn ^:export fetch []
  (xhr/send "/now-playing"
            render-now-playing
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))
