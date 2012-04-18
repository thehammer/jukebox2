(ns jukebox-web.models.library
  (:import [java.io File]
           [java.util UUID])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [jukebox-web.models.db :as db])
  (:use [jukebox-player.tags]
        [jukebox-web.util.file :only (strip-slashes
                                      relative-uri
                                      file-path
                                      mkdir-p
                                      mv
                                      ls
                                      relativize
                                      not-dotfiles
                                      mp3?)]))

;; constants
;; TODO: earmuffs should denote mutable vars, not constants
(def *music-library-title* "Music Library")
(def *music-library* "music")
(def *play-counts-model* "play-counts")
(def *skip-counts-model* "skip-counts")

(defn extension [filename]
  (last (string/split (str filename) #"\.")))

(defn- rename-with-tags [user file]
  (let [{:keys [artist album title]} (extract-tags file)
        dir (file-path *music-library* user (strip-slashes artist) (strip-slashes album))
        new-file (file-path dir (str (strip-slashes title) "." (extension file)))]
    (mkdir-p dir)
    (mv file new-file)))

(defn create-user-directory [username]
  (mkdir-p (file-path *music-library* username)))

(defn save-file [tempfile user ext]
  (let [file-with-ext (io/as-file (file-path *music-library* (str (UUID/randomUUID) "." ext)))]
    (io/copy tempfile file-with-ext)
    (rename-with-tags user file-with-ext)))

(defn parent-directory [path]
  (if (string/blank? path)
    nil
    (relative-uri (relativize *music-library* (.getParent (io/file *music-library* path))))))

(defn list-directory
  ([] (list-directory ""))
  ([path] (ls *music-library* path)))

(defn list-music [path]
  (ls *music-library* path #(and (not-dotfiles %) (mp3? %))))

(defn file-on-disk [relative-path]
  (io/file *music-library* relative-path))

(defn track? [relative-path]
  (.isFile (file-on-disk relative-path)))

(defn all-tracks
  ([]
     (all-tracks ""))
  ([path]
     (let [contents (file-seq (io/file *music-library* path))]
       (->> contents
            (filter #(.endsWith (.getName %) ".mp3"))
            (map #(relativize *music-library* %))))))

(defn owner [song]
  (let [path (.getPath (relativize *music-library* song))
        filename-parts (string/split path #"/")]
    (when (> (count filename-parts) 1)
      (first filename-parts))))

(defn random-song
  ([]
     (random-song ""))
  ([path]
     (let [tracks (all-tracks path)]
       (if-not (empty? tracks)
         (rand-nth (shuffle tracks))
         (random-song "")))))

(defn play-count [track]
  (let [play-count-row (first (db/find-by-field *play-counts-model* "track" (str track)))]
    (or (:count play-count-row) 0)))

(defn skip-count [track]
  (let [skip-count-row (first (db/find-by-field *skip-counts-model* "track" (str track)))]
    (or (:count skip-count-row) 0)))

(defn most-played []
  (let [play-counts (db/find-all *play-counts-model* {"order" ["count" "desc"] "limit" 20})]
    (map #(dissoc % :id) play-counts)))

(defn artist [track]
  (let [relative-file-name (string/replace (.getPath track) (str *music-library* "/") "")
        filename-parts (string/split relative-file-name #"/")]
    (when (> (count filename-parts) 2)
      (second filename-parts))))

(defn most-popular-artists []
  (let [artists (remove nil? (map artist (all-tracks)))
        artists-with-counts (frequencies artists)]
    (take 20 (reverse (sort-by last (into [] artists-with-counts))))))

(defn increment-play-count! [track]
  (let [track-name (str track)
        current-play-count (play-count track)]
    (when (= 0 (db/update *play-counts-model* {:track track-name :count (inc current-play-count)} "track" track-name))
      (db/insert *play-counts-model* {:track track-name :count 1}))))

(defn increment-skip-count! [track]
  (let [track-name (str track)
        current-skip-count (skip-count track)]
    (when (= 0 (db/update *skip-counts-model* {:track track-name :count (inc current-skip-count)} "track" track-name))
      (db/insert *skip-counts-model* {:track track-name :count 1}))))
