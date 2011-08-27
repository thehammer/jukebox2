(ns jukebox-web.models.playlist)

(def *current-song* (atom nil))
(def *queued-songs* (atom []))

(defn music-files []
  (let [contents (file-seq (java.io.File. "music"))]
    (->> contents
      (filter #(.isFile %))
      (filter #(not (= \. (first (.getName %))))))))

(defn- random-song []
  (let [music-file (rand-nth (music-files))
        path (.getPath music-file)]
    {:name (.getName music-file) :track (jukebox.PlayableTrackFactory/build path)}))

(defn current-song []
  @*current-song*)

(defn queued-songs []
  @*queued-songs*)

(defn next-song []
  (first @*queued-songs*))

(defn set-current-song! []
  (if (nil? @*current-song*)
    (if (empty? @*queued-songs*)
      (reset! *current-song* (random-song))
      (do
        (reset! *current-song* (next-song))
        (swap! *queued-songs* rest)))))

(defn skip-current-song! []
  (reset! *current-song* nil))

(defn add-random-song! []
  (swap! *queued-songs* conj (random-song)))

(defn reset-state! []
  (reset! *current-song* nil)
  (reset! *queued-songs* []))
