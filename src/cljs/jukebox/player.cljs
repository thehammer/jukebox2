(ns jukebox.player
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.events :as ev]
            [dommy.template :as template]))

(defn- replace-html [new-content]
  (dom/replace-children! (dom/by-id "player-controls") new-content))

(defn player-controls [playing?]
  (template/node
    (if playing?
      [:a#player-pause {:class "btn"} [:i {:class "icon-pause"}] " Pause"]
      [:a#player-play  {:class "btn"} [:i {:class "icon-play"}] " Play"])))

(defn play [_]
  (xhr/send "/player/play" (fn [_]
                             (replace-html (player-controls true))
                             (attach-events))))

(defn pause [_]
  (xhr/send "/player/pause" (fn [_]
                              (replace-html (player-controls false))
                              (attach-events))))

(defn attach-events []
  (ev/listen! (dom/by-id "player-play") :click play)
  (ev/listen! (dom/by-id "player-pause") :click pause))

(defn render [now-playing]
  (replace-html (player-controls (get-in "player" "playing?")))
  (attach-events))
