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

(defn albums-link [{:keys [artist]}]
  [:a {:href (str "/library/artists/" artist)} artist])

(defn artists [request path artists]
  (layout/main request "browse artists"
               [:h3 "Artists"]
               [:ul.entries
                (map (fn [artist] [:li (albums-link artist)]) artists)] ))

(defn tracks-link [artist {:keys [album]}]
  [:a {:href (str "/library/artists/"  (ring-util/url-encode artist) "/albums/" (ring-util/url-encode album))} album])

(defn albums [request path albums]
  (let [artist (-> request :params :artist)]
    (layout/main request (str "browse albums for " artist)
                 [:h3 (str "Albums of " artist)]
                 [:ul.entries
                  (map (fn [album] [:li (tracks-link artist album)]) albums)])))

(defn item-for-track [logged-in track]
  (if logged-in
    [:li [:a {:href (str "/playlist/add/" (ring-util/url-encode (:title track)))} (:title track)]]
    [:li (:title track)]))

(defn tracks [request tracks]
  (let [artist (-> request :params :artist)
        logged-in (not (nil? (-> request :session :current-user)))
        album (-> request :params :album)]
    (layout/main request (str "Browse Tracks for " artist " - " album)
                 [:h3 (str "Browse Tracks for " artist " - " album)]
                 [:ul.entries
                  (map (partial item-for-track logged-in) tracks)])))
