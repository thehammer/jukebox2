(ns jukebox-web.controllers.application
  (:require [jukebox-web.views.layout :as layout]))

(defn root [request]
  {:status 200
   :body (layout/single-page)})
