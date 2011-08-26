(ns jukebox-web.playlist
  (:require [jukebox-web.views.playlist :as view]))

(def *current-song* (atom nil))

(defn current-song []
  @*current-song*)

(defn music-files []
  (let [contents (file-seq (java.io.File. "music"))]
    (filter #(.isFile %) contents)))

(defn random-song []
  (let [music-file (rand-nth (music-files))
        path (.getPath music-file)]
    {:name (.getName music-file) :track (jukebox.PlayableTrackFactory/build path)}))

(defn set-current-song! []
  (if (nil? @*current-song*)
    (reset! *current-song* (random-song))))

(defn skip-current-song! []
  (reset! *current-song* nil))

(defn index [request]
  (view/index (current-song)))
