(ns jukebox-web.views.forms
(:use [hiccup core form-helpers]))

(defn labeled-field [field-type id label-text errors]
  [:div
    (label id label-text)
    (field-type id)
    [:span (id errors)]])

