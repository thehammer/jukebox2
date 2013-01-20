(ns jukebox-web.models.library
  (:import [java.io File]
           [java.util UUID])
  (:require [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.java.jdbc :as sql]
            [jukebox-web.models.artwork :as artwork]
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
                                      has-known-extension?)]))

;; constants
;; TODO: earmuffs should denote mutable vars, not constants
(def ^:dynamic *music-library-title* "Music Library")
(def ^:dynamic *music-library* "music")
(def play-counts-model :play_counts)
(def skip-counts-model :skip_counts)

(def tracks-users-type-owner "OWNER")

(defn nested-location [file-name]
  (str (apply str (interpose File/separator (take 5 file-name)))
       File/separator
       file-name))

(defn extension [filename]
  (last (cstr/split (str filename) #"\.")))

(defn find-all []
  (db/find-all ["SELECT * FROM tracks"]))

(defn find-by-id [id]
  (first (db/find-by-field :tracks :id id)))

(defn save-file! [tempfile owner]
  (let [{:keys [artist album title duration]} (extract-tags tempfile)
        {:keys [large extra-large]} (artwork/album-cover album artist)
        location (nested-location (str (UUID/randomUUID) "." (extension tempfile)))]
    (.mkdirs (.getParentFile (io/file *music-library* location)))
    (io/copy (io/as-file tempfile) (io/file *music-library* location))
    (let [track (db/insert :tracks {:tempfile_location tempfile
                                    :artist artist
                                    :album album
                                    :title title
                                    :duration_secs duration
                                    :large_image large
                                    :xlarge_image extra-large
                                    :location location
                                    :play_count 0
                                    :skip_count 0})]
      (db/insert :tracks_users {:track_id (:id track)
                                :user_id (:id owner)
                                :type tracks-users-type-owner})
      track)))

(defn owner-md [track]
  (db/find-first [(str "SELECT u.* "
                       "FROM users u "
                       "INNER JOIN tracks_users tu ON tu.user_id = u.id "
                       "WHERE tu.track_id = ?") (:id track)]))

(defn all-artists []
  (db/find-all ["SELECT DISTINCT artist FROM tracks"]))

(defn albums-for-artist [artist]
  (db/find-all ["SELECT DISTINCT album FROM tracks WHERE artist = ?" artist]))

(defn tracks-for-artists-album [artist album]
  (db/find-all ["SELECT * FROM tracks WHERE artist = ? and album = ?" artist album]))

(defn- rename-with-tags [user file]
  (let [{:keys [artist album title]} (extract-tags file)
        dir (file-path *music-library* user (strip-slashes artist) (strip-slashes album))
        new-file (file-path dir (str (strip-slashes title) "." (extension file)))]
    (mkdir-p dir)
    (mv file new-file)))

(defn save-file [tempfile user ext]
  (let [file-with-ext (io/as-file (file-path *music-library* (str (UUID/randomUUID) "." ext)))]
    (io/copy tempfile file-with-ext)
    (rename-with-tags user file-with-ext)))

(defn parent-directory [path]
  (if (cstr/blank? path)
    nil
    (relative-uri (relativize *music-library* (.getParent (io/file *music-library* path))))))

(defn list-directory
  ([] (list-directory ""))
  ([path] (ls *music-library* path)))

(defn list-music [path]
  (ls *music-library* path #(and (not-dotfiles %) (has-known-extension? %))))

(defn file-on-disk [{:keys [location]}]
  (io/file *music-library* location))

(defn track? [relative-path]
  (.isFile (file-on-disk relative-path)))

(defn all-tracks
  ([] (all-tracks ""))
  ([path]
     (let [contents (file-seq (io/file *music-library* path))]
       (->> contents
            (filter #(has-known-extension? %))
            (map #(relativize *music-library* %))))))

(defn owner [song]
  (let [path (.getPath (relativize *music-library* song))
        filename-parts (cstr/split path #"/")]
    (when (> (count filename-parts) 1)
      (first filename-parts))))

(defn count-tracks-owned-by [{:keys [id]}]
  (:count (db/find-first [(str "SELECT count(*) AS count "
                               "FROM tracks t "
                               "INNER JOIN tracks_users tu ON t.id = tu.track_id "
                               "WHERE tu.user_id = ?")
                          id])))

(defn random-track-owned-by [user]
  (let [offset (inc (rand-int (dec (count-tracks-owned-by user))))]
    (db/find-first [(str "SELECT * FROM ( "
                         "  SELECT ROW_NUMBER() OVER() AS rownum, t.* "
                         "  FROM tracks t "
                         "  INNER JOIN tracks_users tu ON t.id = tu.track_id "
                         "  WHERE tu.user_id = ? "
                         ") AS tmp "
                         "WHERE rownum = ?")
                    (:id user)
                    offset])))

(defn count-tracks []
  (:count (db/find-first ["SELECT count(*) AS count FROM tracks"])))

(defn random-track []
  (let [offset (inc (rand-int (dec (count-tracks))))]
    (db/find-first [(str "SELECT * FROM ( "
                         "  SELECT ROW_NUMBER() OVER() AS rownum, t.* "
                         "  FROM tracks t "
                         ") AS tmp "
                         "WHERE rownum = ?")
                    offset])))

(defn random-song
  ([] (random-track))
  ([user] (random-track-owned-by user)))

(defn play-count [track]
  (let [play-count-row (first (db/find-by-field play-counts-model "track" (str track)))]
    (or (:count play-count-row) 0)))

(defn skip-count [track]
  (let [skip-count-row (first (db/find-by-field skip-counts-model "track" (str track)))]
    (or (:count skip-count-row) 0)))

(defn most-played []
  (db/find-all [(str "SELECT * FROM ("
                       "SELECT ROW_NUMBER() OVER() AS rownum, play_counts.* "
                       "FROM play_counts "
                       "ORDER BY count DESC "
                      ") AS tmp "
                      "WHERE rownum <= 20 ")]))

(defn artist [track]
  (let [relative-file-name (cstr/replace (.getPath track) (str *music-library* "/") "")
        filename-parts (cstr/split relative-file-name #"/")]
    (when (> (count filename-parts) 2)
      (second filename-parts))))

(defn most-popular-artists []
  (let [artists (remove nil? (map artist (all-tracks)))
        artists-with-counts (frequencies artists)]
    (take 20 (reverse (sort-by last (into [] artists-with-counts))))))

(defn increment-play-count! [track]
  (let [track-name (str track)
        current-play-count (play-count track)]
    (if (= 0 current-play-count)
      (db/insert play-counts-model {:track track-name :count 1})
      (db/update play-counts-model {:track track-name :count (inc current-play-count)} :track track-name))))

(defn increment-skip-count! [track]
  (let [track-name (str track)
        current-skip-count (skip-count track)]
    (if (= 0 current-skip-count)
      (db/insert skip-counts-model {:track track-name :count 1})
      (db/update skip-counts-model {:track track-name :count (inc current-skip-count)} "track" track-name))))
