(ns jukebox.stats
  (:require [jukebox.core :as jukebox]
            [domina :as dom]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.window :as window]))

(defn render-user-stats [stats]
  (template/node
    [:h1 "User Stats"]))

(defn show-user-stats [event]
  (ev/prevent-default event)
  (dom/log "stats..")
  (dom/replace-children (dom/by-id "content") (render-user-stats)))

(defn attach-events []
  (ev/listen-once! (dom/by-id "stats-user") :click show-user-stats))

(window/register-onload! attach-events)
