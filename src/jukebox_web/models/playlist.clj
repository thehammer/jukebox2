(ns jukebox-web.models.playlist
  (:require [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]))

(def current-song-atom (atom nil))
(def queued-songs-atom (atom []))
(def recent-songs-atom (atom clojure.lang.PersistentQueue/EMPTY))

(def *recent-songs-factor* 0.25)

(defn- random-song-for-enabled-user []
  (let [users (user/find-enabled)]
    (if (not (empty? users))
      (library/random-song (:login (rand-nth users)))
      (library/random-song))))

(defn- random-song []
  (let [song (random-song-for-enabled-user)]
    (if-not (nil? song)
      (.getPath song))))

(defn current-song []
  @current-song-atom)

(defn queued-songs []
  @queued-songs-atom)

(defn- recent-songs-to-keep []
  (* (count (library/all-tracks)) *recent-songs-factor*))

(defn add-song! [song]
  (swap! queued-songs-atom conj (library/file-on-disk song))
  (swap! recent-songs-atom conj song)
  (if (< (recent-songs-to-keep) (count @recent-songs-atom))
    (swap! recent-songs-atom pop)))

(defn add-random-song! []
  (loop [song (random-song) attempts 0]
    (if (or (nil? song) (.contains @recent-songs-atom song))
      (recur (random-song) (inc attempts))
      (add-song! song))))

(defn reset-state! []
  (reset! current-song-atom nil)
  (reset! queued-songs-atom [])
  (reset! recent-songs-atom (clojure.lang.PersistentQueue/EMPTY)))

(defn- move-to-next-track! []
  (reset! current-song-atom (first @queued-songs-atom))
  (swap! queued-songs-atom (comp vec rest)))

(defn next-track [_]
  (if-let [queued (first @queued-songs-atom)]
    (move-to-next-track!)
    (do (add-random-song!) (move-to-next-track!)))
  @current-song-atom)

(defn playlist-seq []
  (iterate next-track (next-track "")))
