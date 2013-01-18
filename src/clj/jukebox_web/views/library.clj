(ns jukebox-web.views.library
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [clojure.string :as string]
            [ring.util.codec :as ring-util])
  (:use [hiccup core page form element]
        [jukebox-player.tags]
        [jukebox-web.util.file :only (relative-uri)]))

(defn link-or-string [file request]
  (if (user/canAdd? (-> request :session :current-user))
    (link-to (str "/playlist/add/" (relative-uri file))
             (.getName file)
             " ("
             (library/play-count (library/file-on-disk file))
             ")")
    (str (.getName file) " (" (library/play-count (library/file-on-disk file)) ")")))

(defn album-link [album-path user]
  (when (and (not= album-path library/*music-library-title*)
             (user/canAdd? user))
    (link-to (str "/playlist/add-album/" (ring-util/url-encode album-path))
             "Add Album!")))

(defn display-file [file request]
  (if (library/track? file)
    (link-or-string file request)
    (link-to (str "/library/browse/" (relative-uri file)) (.getName file))))

(defn browse [request path files]
  (let [parent-path (library/parent-directory path)]
    (layout/main request "browse library"
                 [:h3 "Files in " path " (play count)"]
                 (album-link path (-> request :session :current-user))
                 [:ul.entries
                  (if-not (nil? parent-path) [:li (link-to (str "/library/browse/" parent-path) "..")])
                  (map #(vector :li (display-file % request)) (sort files))])))
