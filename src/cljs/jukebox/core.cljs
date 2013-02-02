(ns jukebox.core
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [jukebox.window :as window]))

(def current (atom {}))
(def player-state (atom {}))
(def playlist-state (atom {}))
(def current-user-state (atom {}))

(defn handle-poll-response [response]
  (let [data (js->clj (.getResponseJson (.-target response)))]
    (if-not (= data @current)
      (reset! current data))))

(defn sync-playlist-state [_ _ _ new-state]
  (let [playlist (select-keys new-state ["current-song" "queued-songs"])]
    (when-not (= playlist @playlist-state)
      (reset! playlist-state playlist))))

(defn sync-player-state [_ _ _ new-state]
  (let [player (select-keys new-state ["player"])]
    (when-not (= player @player-state)
      (reset! player-state player))))

(defn sync-current-user-state [_ _ _ new-state]
  (let [current-user (get new-state "current-user")]
    (when-not (= current-user @current-user-state)
      (reset! current-user-state current-user))))

(add-watch current :player-state sync-player-state)
(add-watch current :playlist-state sync-playlist-state)
(add-watch current :current-user-state sync-current-user-state)

(defn ^:export poll []
  (xhr/send "/now-playing"
            handle-poll-response
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))

(js/setInterval poll 2000)
(window/register-onload! poll)
