(ns jukebox-web.models.playlist)

(def *current-song* (atom nil))
(def *queued-songs* (atom []))

(defn music-files []
  (let [contents (file-seq (java.io.File. "music"))]
    (->> contents
      (filter #(.isFile %))
      (filter #(not (= \. (first (.getName %))))))))

(defn- random-song []
  (let [music-file (rand-nth (music-files))]
    (.getPath music-file)))

(defn current-song []
  @*current-song*)

(defn queued-songs []
  @*queued-songs*)

(defn add-song! [song]
  (swap! *queued-songs* conj song))

(defn add-random-song! []
  (add-song! (random-song)))

(defn reset-state! []
  (reset! *current-song* nil)
  (reset! *queued-songs* []))

(defn- next-track [_]
  (if-let [queued (first @*queued-songs*)]
    (do (swap! *queued-songs* rest) (reset! *current-song* queued))
    (reset! *current-song* (random-song)))
  @*current-song*)

(defn playlist-seq []
  (iterate next-track (next-track "")))
