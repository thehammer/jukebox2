(ns jukebox-web.controllers.library
  (:require [jukebox-web.views.library :as view]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]))

(defn upload [request]
  (when-let [current-user (-> request :session :current-user)]
    (let [{:keys [tempfile filename]} (-> request :params :file)]
    (library/save-file tempfile current-user (library/extension filename)))
  "upload complete"))

(defn browse-root [request]
  (view/browse request library/*music-library-title* (library/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (library/list-directory path)]
    (view/browse request path files)))

(defn artists [request]
  (view/artists request library/*music-library-title* (library/all-artists)))

(defn albums-for-artist [request]
  (view/albums request  "foo" (library/albums-for-artist (-> request :params :artist))))
