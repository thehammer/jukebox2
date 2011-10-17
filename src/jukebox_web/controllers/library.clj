(ns jukebox-web.controllers.library
  (:require [jukebox-web.views.library :as view]
            [jukebox-web.models.library :as lib]
            [jukebox-web.models.user :as user]))

(defn upload [request]
  (when-let [current-user (-> request :session :current-user)]
    (let [{:keys [tempfile filename]} (-> request :params :file)]
    (lib/save-file tempfile current-user (lib/extension filename)))
  "upload complete"))

(defn browse-root [request]
  (view/browse request "Music Library" (lib/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (lib/list-directory path)]
    (view/browse request path files)))
