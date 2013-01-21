(ns jukebox.player
  (:require [jukebox.state :as state]
            [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.events :as ev]
            [dommy.template :as template]))

(defn player-controls [state]
  (template/node
    (if (get-in state ["player" "playing?"])
      [:a#player-pause {:class "btn"} [:i {:class "icon-pause"}] " Pause"]
      [:a#player-play  {:class "btn"} [:i {:class "icon-play"}] " Play"])))

(defn toggle-playing [state]
  (update-in state ["player" "playing?"] not))

(defn play [_]
  (xhr/send "/player/play" (fn [_] (swap! state/current toggle-playing))))

(defn pause [_]
  (xhr/send "/player/pause" (fn [_] (swap! state/current toggle-playing))))

(defn render [state]
  (dom/replace-children! (dom/by-id "player-controls")
                         (player-controls state))
  (ev/listen! (dom/by-id "player-play") :click play)
  (ev/listen! (dom/by-id "player-pause") :click pause))

(add-watch state/current
           :player-controls
           (fn [_ _ _ state] (render state)))
