(ns jukebox-web.views.library
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.library :as library])
  (:use [hiccup core page-helpers]
        [hiccup core form-helpers]
        [jukebox-player.tags]
        [jukebox-web.util.file :only (relative-uri)]
        [clojure.string :only (join split)]))

(defn display-file [file]
  (if (library/track? file)
    (link-to (str "/playlist/add/" (relative-uri file)) (.getName file) " (" (library/play-count (library/file-on-disk file)) ")")
    (link-to (str "/library/browse/" (relative-uri file)) (.getName file))))

(defn browse [request path files]
  (let [parent-path (library/parent-directory path)]
  (layout/main request "browse library"
     [:h3 "files in " path " (play count)"]
     [:ul
       (if-not (nil? parent-path) [:li (link-to (str "/library/browse/" parent-path) "..")])
       (map #(vector :li (display-file %)) files)])))
