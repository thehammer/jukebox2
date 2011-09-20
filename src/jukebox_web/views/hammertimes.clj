(ns jukebox-web.views.hammertimes
  (:require [jukebox-web.views.layout :as layout])
  (:use [hiccup core form-helpers page-helpers]
        [jukebox-web.views.forms]))

(defn create [errors]
  (layout/main "Create Hammertime"
     (form-to [:post "/hammertime"]
       (labeled-field text-field :name "Name" errors)
       (labeled-field text-field :path "Path" errors)
       (labeled-field text-field :start "Start" errors)
       (labeled-field text-field :end "End" errors)
       (submit-button "Create"))))
