(ns jukebox-web.views.library
  (:require [jukebox-web.views.layout :as layout])
  (:use [hiccup core page-helpers]
        [hiccup core form-helpers]
        [jukebox-player.tags]
        [ring.util.codec :only (url-encode)]
        [clojure.string :only (join split)]))

(defn- relative-uri [file]
   (url-encode (.getPath file)))

(defn display-file [file]
  (if (.isFile file)
    (link-to (str "/playlist/add/" (relative-uri file)) (.getPath file))
    (link-to (str "/library/browse/" (relative-uri file)) (.getPath file))))

(defn browse [path files]
  (layout/main "browse library"
     [:h3 "files in " path]
     [:ul (map #(vector :li (display-file %)) files)]))
