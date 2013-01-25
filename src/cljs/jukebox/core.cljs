(ns jukebox.core
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [jukebox.window :as window]))

(def current (atom {}))
(def player-state (atom {}))
(def playlist-state (atom {}))

(defn handle-poll-response [response]
  (let [data (js->clj (.getResponseJson (.-target response)))
        player (select-keys data ["player"])
        playlist (select-keys data ["current-song" "queued-songs"])]
    (if-not (= data @current)
      (reset! current data))
    (when-not (= player @player-state)
      (reset! player-state player))
    (when-not (= playlist @playlist-state)
      (reset! playlist-state playlist))))

(defn ^:export poll []
  (xhr/send "/now-playing"
            handle-poll-response
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))

(js/setInterval poll 2000)
(window/register-onload! poll)
