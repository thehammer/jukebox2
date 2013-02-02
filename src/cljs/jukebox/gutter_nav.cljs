(ns jukebox.gutter-nav
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.core :as jukebox]))

(def active-nav (atom "now-playing"))
(def gutter-events (atom []))

(defn nav-item [current-nav element-id label]
  (if (= element-id current-nav)
    [:li.active [:a {:id element-id :href "#"} label]]
    [:li [:a {:id element-id :href "#"} label]]))

(defn render-gutter [current-user current-nav]
  (let [current-nav-item (partial nav-item current-nav)]
    (template/node
      [:div.well
        [:ul#gutter-nav.nav.nav-list
          (current-nav-item "now-playing" "Now Playing")
          [:li.divider]
          [:li.nav-header "Library"]
          (current-nav-item "library-browse" "Browse")
          (if current-user
            (current-nav-item "library-upload" "Upload")
            [:li.disabled [:a#library-upload "Upload"]])
          [:li.divider]
          [:li.nav-header "Stats"]
          (current-nav-item "stats-user" "Users")]])))

(defn add-gutter-event [element-id callback-fn]
  (swap! gutter-events conj [element-id (fn [event]
                                          (ev/prevent-default event)
                                          (callback-fn event)
                                          (reset! active-nav element-id))]))

(defn attach-gutter-events []
  (doseq [[id fn] @gutter-events]
    (ev/listen! (dom/by-id id) :click fn)))

(defn show-gutter [current-user current-nav]
  (dom/replace-children! (dom/by-id "gutter")
                         (render-gutter current-user current-nav))
  (attach-gutter-events))

(add-watch jukebox/current-user-state :gutter (fn [_ _ _ user-state]
                                                (show-gutter user-state @active-nav)))

(add-watch active-nav :gutter (fn [_ _ _ nav-state]
                                (show-gutter @jukebox/current-user-state nav-state)))
