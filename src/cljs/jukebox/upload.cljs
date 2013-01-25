(ns jukebox.upload
  (:require [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [goog.debug :as debug]
            [jukebox.gutter-nav :as nav]
            [jukebox.window :as window]))

(defn render-upload []
  (template/node
    [:div#uploader
      [:div#uploader-dropzone "Drop Files Here"]
      [:div.in-progress
        [:table.table
          [:thead
            [:tr
              [:th "File"]
              [:th "Progress"]
              [:th "Status"]
              [:th ""]]]]]]))

(defn upload-file [event]
  (ev/prevent-default event)
  (.log js/console (debug/expose (ev/raw-event event)))
)

(defn show-upload [event]
  (nav/make-active! (.-parentNode (ev/target event)))
  (dom/replace-children! (dom/by-id "content") (render-upload))
  (attach-events))

(defn attach-events []
  (ev/listen! (dom/by-id "uploader-dropzone") :drop upload-file)
  (ev/listen-once! (dom/by-id "library-upload") :click show-upload))

(window/register-onload! attach-events)
