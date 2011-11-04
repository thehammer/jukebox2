(ns jukebox-web.views.hammertimes
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.library :as library])
  (:use [hiccup core form-helpers page-helpers]
        [jukebox-web.util.file :only (relative-uri)]
        [jukebox-web.views.forms]))

(defn- form-fields [hammertime errors]
  [:div
   (labeled-field text-field :file "File" (:file hammertime) errors)
   (labeled-field text-field :name "Name" (:name hammertime) errors)
   (labeled-field text-field :start "Start" (:start hammertime) errors)
   (labeled-field text-field :end "End" (:end hammertime) errors)])

(defn create [request errors]
  (layout/main request "Create Hammertime"
     (form-to [:post "/hammertimes"]
       (form-fields {:file (-> request :params :file)} errors)
       (primary-submit-button "Create"))))

(defn- show-hammertime [hammertime]
  [:tr
    [:td (:name hammertime)]
    [:td (:file hammertime)]
    [:td (:start hammertime)]
    [:td (:end hammertime)]
    [:td
      (form-to [:post "/hammertimes/play"]
        (hidden-field :name (:name hammertime))
        [:input {:type "submit" :value "Play" :class "btn primary"}])
     [:a {:href (str "/hammertimes/" (:id hammertime) "/edit") :class "btn"} "Edit"]
      (form-to [:post (str "/hammertimes/" (:id hammertime) "/delete")]
        [:input {:type "submit" :value "Delete" :class "btn danger"}])]])

(defn index [request hammertimes]
  (layout/main request "Hamertimes"
    [:table
      [:tr [:th "Name"] [:th "File"] [:th "Start"] [:th "End"] [:th]]
      (map show-hammertime hammertimes)]
    [:a {:href "/hammertimes/browse"} [:button.btn.success "Add"]]))

(defn edit [request hammertime errors]
  (layout/main request "Edit Hammertime"
     (form-to [:post (str "/hammertimes/" (:id hammertime) "/update")]
       (form-fields hammertime errors)
       (primary-submit-button "Update"))))

(defn display-file [file]
  (if (library/track? file)
    (link-to (str "/hammertimes/new/" (relative-uri file)) (.getName file))
    (link-to (str "/hammertimes/browse/" (relative-uri file)) (.getName file))))

(defn browse [request path files]
  (layout/main request "browse library"
     [:h3 "files in " path]
     [:ul (map #(vector :li (display-file %)) files)]))
