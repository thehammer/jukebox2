(ns jukebox-web.models.playlist
  (:require [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.user :as user]
            [clojure.java.jdbc :as sql]
            [jukebox-web.models.db :as db])
  (:import [java.util UUID]))

(def current-song-atom (atom nil))
(def queued-songs-atom (atom []))
(def recent-songs-atom (atom clojure.lang.PersistentQueue/EMPTY))

(def ^{:dynamic true} *recent-songs-factor* 0.25)
(def ^{:dynamic true} *weight-threshold* 35)

(defn- expand-by-weight [user]
  (let [weight (if (>= (library/count-tracks-owned-by user) *weight-threshold*) 5 1)]
    (take weight (repeat user))))

(defn weighted-users []
  (flatten (map expand-by-weight (user/find-enabled))))

(defn- random-song-for-enabled-user []
  (let [users (user/find-enabled)]
    (if (not (empty? users))
      (library/random-song (rand-nth (shuffle (weighted-users))))
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

(defn add-song! [track & [login]]
  (let [playlist-track (assoc track :playlist-id (str (UUID/randomUUID))
                                    :requester (or login "randomizer"))]
    (swap! queued-songs-atom conj playlist-track)
    (swap! recent-songs-atom conj playlist-track)
    (if (< (recent-songs-to-keep) (count @recent-songs-atom))
      (swap! recent-songs-atom pop))))

(defn add-album! [artist album & [requester]]
  (doseq [track (library/tracks-for-artists-album artist album)]
    (add-song! track requester)))

(defn- recent? [track]
  (some #(= (:id track) (:id %)) @recent-songs-atom))

(defn add-random-song! []
  (loop [song (random-song-for-enabled-user) attempts 0]
    (if (recent? song)
      (recur (random-song-for-enabled-user) (inc attempts))
      (add-song! song {:login "(randomizer)"}))))

(defn queued-song [playlist-id]
  (first (filter #(= (:playlist-id %) playlist-id) (queued-songs))))

(defn delete-song! [playlist-id]
  (let [filter-func (fn [queue] (filter #(not (= (:playlist-id %) playlist-id)) queue))]
    (swap! queued-songs-atom (comp vec filter-func))))

(defn reset-state! []
  (reset! current-song-atom nil)
  (reset! queued-songs-atom [])
  (reset! recent-songs-atom (clojure.lang.PersistentQueue/EMPTY)))

(defn- move-to-next-track! []
  (reset! current-song-atom (first @queued-songs-atom))
  (library/increment-play-count! (:id @current-song-atom))
  (swap! queued-songs-atom (comp vec rest)))

(defn next-track [_]
  (if-let [queued (first @queued-songs-atom)]
    (move-to-next-track!)
    (do (add-random-song!) (move-to-next-track!)))
  @current-song-atom)

(defn playlist-seq []
  (iterate (comp library/file-on-disk next-track) (library/file-on-disk (next-track ""))))
