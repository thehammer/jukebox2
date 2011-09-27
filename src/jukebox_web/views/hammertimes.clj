(ns jukebox-web.views.hammertimes
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.library :as library])
  (:use [hiccup core form-helpers page-helpers]
        [jukebox-web.util.file :only (relative-uri)]
        [jukebox-web.views.forms]))

(defn create [request errors]
  (layout/main request "Create Hammertime"
     (form-to [:post "/hammertimes"]
       (hidden-field :file (-> request :params :file))
       (labeled-field text-field :name "Name" errors)
       (labeled-field text-field :start "Start" errors)
       (labeled-field text-field :end "End" errors)
       (submit-button "Create"))))

(defn- show-hammertime [hammertime]
  [:tr
    [:td (:name hammertime)]
    [:td
      (form-to [:post "/hammertimes/play"]
        (hidden-field :name (:name hammertime))
        [:input {:type "submit" :value "Play" :class "btn primary"}])
      (form-to [:post (str "/hammertimes/" (:id hammertime) "/delete")]
        [:input {:type "submit" :value "Delete" :class "btn danger"}])]])

(defn index [request hammertimes]
  (layout/main request "Hamertimes"
    [:table
      [:tr [:th "Name"] [:th]]
      (map show-hammertime hammertimes)]
    [:a {:href "/hammertimes/browse"} [:button.btn.success "Add"]]))

(defn display-file [file]
  (if (library/track? file)
    (link-to (str "/hammertimes/new/" (relative-uri file)) (.getName file))
    (link-to (str "/hammertimes/browse/" (relative-uri file)) (.getName file))))

(defn browse [request path files]
  (layout/main request "browse library"
     [:h3 "files in " path]
     [:ul (map #(vector :li (display-file %)) files)]))
