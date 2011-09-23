(ns jukebox-web.controllers.library
  (:require [jukebox-web.views.library :as view]
            [jukebox-web.models.library :as lib]
            [jukebox-web.models.user :as user]))

(defn upload [request]
  (let [{:keys [tempfile filename]} (-> request :params :file)
        user (-> request :params :user)]
    (lib/save-file tempfile user (lib/extension filename)))
  "upload complete")

(defn browse-root [request]
  (view/browse "Music Library" (lib/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (lib/list-directory path)]
    (view/browse path files)))
