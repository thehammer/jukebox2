(ns jukebox-web.models.playlist
  (:require [jukebox-web.models.library :as library]))

(def *current-song* (atom nil))
(def *queued-songs* (atom []))
(def recent-songs (atom clojure.lang.PersistentQueue/EMPTY))

(defn- random-song []
  (let [music-file (rand-nth (library/all-tracks))]
    (.getPath music-file)))

(defn current-song []
  @*current-song*)

(defn queued-songs []
  @*queued-songs*)

(defn- recent-songs-to-keep []
  (* (count (library/all-tracks)) 0.25))

(defn add-song! [song]
  (swap! *queued-songs* conj (library/file-on-disk song))
  (swap! recent-songs conj song)
  (if (< (recent-songs-to-keep) (count @recent-songs))
    (swap! recent-songs pop)))

(defn add-random-song! []
  (loop [song (random-song)]
    (if (.contains @recent-songs song)
      (recur (random-song))
      (add-song! song))))

(defn reset-state! []
  (reset! *current-song* nil)
  (reset! *queued-songs* [])
  (reset! recent-songs (clojure.lang.PersistentQueue/EMPTY)))

(defn- move-to-next-track! []
  (reset! *current-song* (first @*queued-songs*))
  (swap! *queued-songs* (comp vec rest)))

(defn next-track [_]
  (if-let [queued (first @*queued-songs*)]
    (move-to-next-track!)
    (do (add-random-song!) (move-to-next-track!)))
  @*current-song*)

(defn playlist-seq []
  (iterate next-track (next-track "")))
