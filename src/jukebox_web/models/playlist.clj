(ns jukebox-web.models.playlist
  (:require [jukebox-web.models.library :as library]))

(def *current-song* (atom nil))
(def *queued-songs* (atom []))

(defn- random-song []
  (let [music-file (rand-nth (library/all-tracks))]
    (.getPath music-file)))

(defn current-song []
  @*current-song*)

(defn queued-songs []
  @*queued-songs*)

(defn add-song! [song]
  (swap! *queued-songs* conj (library/file-on-disk song)))

(defn add-random-song! []
  (add-song! (random-song)))

(defn reset-state! []
  (reset! *current-song* nil)
  (reset! *queued-songs* []))

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
