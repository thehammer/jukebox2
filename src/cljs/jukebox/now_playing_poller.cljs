(ns jukebox.now-playing-poller
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [dommy.template :as template]))

(defn cover-flow [now-playing]
  (template/node
    [:div.#playlist-flow.ContentFlow
      [:div.loadIndicator [:div.indicator]]
      [:div.flow
        [:img.item {:src (get-in now-playing ["current-song" "xlarge_image"])
                    :title (str (get-in now-playing ["current-song" "artist"])
                                " - "
                                (get-in now-playing ["current-song" "title"]))}]
        [:img.item {:src (get-in now-playing ["current-song" "xlarge_image"])
                    :title (str (get-in now-playing ["current-song" "artist"])
                                " - "
                                (get-in now-playing ["current-song" "title"]))}]]
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
    (dom/replace-children! (dom/by-id "main")
                           (cover-flow data))
    (._init (js/ContentFlow. "playlist-flow"))
    (dom/log (current-track data))))

(defn ^:export fetch []
  (xhr/send "/now-playing"
            render-now-playing
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))
