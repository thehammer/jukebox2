(ns jukebox.gutter-nav
  (:require [goog.net.XhrIo :as xhr]
            [domina.css :as css]
            [domina :as dom]))

(defn make-active! [node]
  (dom/remove-class! (css/sel "#gutter-nav li.active") "active")
  (dom/add-class! node "active"))
