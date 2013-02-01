(ns jukebox-web.controllers.library
  (:require [cheshire.core :as json]
            [jukebox-web.views.library :as view]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]))

(defn upload [request]
  (let [current-user "randomizer"]
  ;(when-let [current-user (-> request :session :current-user)]
    (let [{:keys [tempfile filename]} (-> request :params :file)]
      (library/save-file! tempfile filename (user/find-by-login current-user))
  "upload complete")))

(defn browse-root [request]
  (view/browse request library/*music-library-title* (library/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (library/list-directory path)]
    (view/browse request path files)))

(defn artists [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string (library/all-artists))})

(defn albums-for-artist [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string (library/albums-for-artist (-> request :params :artist)))})

(defn tracks-for-album [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string (library/tracks-for-artists-album (-> request :params :artist)
                                                                 (-> request :params :album)))})
