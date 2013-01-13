(ns jukebox-web.views.forms
(:use [hiccup core form]))

(defn labeled-field
  ([field-type id label-text errors]
    [:div
      (label id label-text)
      (field-type id)
      [:span (id errors)]])
  ([field-type id label-text value errors]
    [:div
      (label id label-text)
      (field-type id value)
      [:span (id errors)]]))

(defn primary-submit-button [value]
  [:input.primary.btn {:type "submit" :value value}])

(defn danger-submit-button [value]
  [:input.btn.danger {:type "submit" :value value}])

(defn success-submit-button [value]
  [:input.btn.success {:type "submit" :value value}])
