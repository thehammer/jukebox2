(ns jukebox-web.models.playlist
  (:require [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.user :as user])
  (:import [java.util UUID]))

(def current-song-atom (atom nil))
(def queued-songs-atom (atom []))
(def recent-songs-atom (atom clojure.lang.PersistentQueue/EMPTY))

(def *recent-songs-factor* 0.25)
(def *weight-threshold* 35)

(defn- enabled-with-counts []
  (let [users (user/find-enabled)
        users-with-counts (map #(vector (:login %) (user/count-songs %)) users)]
    users-with-counts))

(defn- threshold [songs]
  (if (>= songs *weight-threshold*) 5 1))

(defn weighted-users []
  (let [expanded-set (atom [])]
    (doseq [user (enabled-with-counts)]
      (let [songs (get user 1)
            login (get user 0)]
        (dotimes [n (threshold songs)]
          (swap! expanded-set conj login))))
  @expanded-set))

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

(defn add-song! [song & [user]]
  (let [uuid (.toString (UUID/randomUUID))
        track (playlist-track/new-playlist-track (library/file-on-disk song) user uuid)]
    (swap! queued-songs-atom conj track)
    (swap! recent-songs-atom conj track)
    (if (< (recent-songs-to-keep) (count @recent-songs-atom))
      (swap! recent-songs-atom pop))))

(defn add-album! [album-directory & [user]]
  (let [album-songs (library/list-music album-directory)]
    (doseq [song album-songs] (add-song! song user))))

(defn add-random-song! []
  (loop [song (random-song) attempts 0]
    (if (and (< attempts 4)
             (or (nil? song)
                 (.contains @recent-songs-atom song)))
      (recur (random-song) (inc attempts))
      (add-song! song {:login "(randomizer)"}))))

(defn queued-song [id]
  (first (filter #(= (:id %) id) (queued-songs))))

(defn delete-song! [id]
  (let [filter-func (fn [queue] (filter #(not (= (:id %) id)) (queued-songs)))]
    (swap! queued-songs-atom (comp vec filter-func))))

(defn reset-state! []
  (reset! current-song-atom nil)
  (reset! queued-songs-atom [])
  (reset! recent-songs-atom (clojure.lang.PersistentQueue/EMPTY)))

(defn- move-to-next-track! []
  (reset! current-song-atom (first @queued-songs-atom))
  (library/increment-play-count! (:song @current-song-atom))
  (swap! queued-songs-atom (comp vec rest)))

(defn next-track [_]
  (if-let [queued (first @queued-songs-atom)]
    (move-to-next-track!)
    (do (add-random-song!) (move-to-next-track!)))
  (:song @current-song-atom))

(defn playlist-seq []
  (iterate next-track (next-track "")))
