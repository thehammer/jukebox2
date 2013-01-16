(ns jukebox.example
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [jukebox.util :as util]
            [jukebox.templates :as t]))

(defn update-current-track [response]
  (let [data (js->clj (.getResponseJson (.-target response)))]
    (dom/log data)
    (t/replace-with-template "now-playing"
                             "now-playing-template"
                             data)))

(defn ^:export fetch-current-track []
  (xhr/send "/playlist/current-track"
            update-current-track
            "GET"
            nil
            (util/map->js-obj {"Accept" "application/json"})))
