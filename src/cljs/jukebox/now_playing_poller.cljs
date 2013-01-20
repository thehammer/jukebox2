(ns jukebox.now-playing-poller
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [dommy.template :as template]))

(defn current-track [now-playing]
  (template/node
    [:img {:src (get-in now-playing ["current-song" "large_image"])}]))

(defn render-now-playing [response]
  (let [data (js->clj (.getResponseJson (.-target response)))]
    (dom/log data)
    (dom/replace-children! (dom/by-id "current-track")
                           (current-track data))
    (dom/log (current-track data))))

(defn ^:export fetch []
  (xhr/send "/now-playing"
            render-now-playing
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))
