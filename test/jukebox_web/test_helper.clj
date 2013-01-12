(ns jukebox-web.test-helper
  (:require [fleetdb.embedded :as fleetdb]
            [fs.core :as fs]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.artwork :as artwork]
            [jukebox-web.models.playlist :as playlist])
  (:use [clojure.contrib.mock]))

(defn with-database-connection [spec]
  (let [connection (fleetdb/init-ephemeral)]
    (binding [db/*db* connection]
      (spec))
    (fleetdb/close connection)))

(defn with-test-music-library [spec]
  (binding [library/*music-library* "test/music"]
    (fs/delete-dir "test/music")
    (fs/copy-dir "test/fixtures/music" "test/music")
    (expect [artwork/album-cover (returns "no_art_lrg.png")]
    (spec))))

(defn with-smaller-weight-threshold [spec]
  (binding [playlist/*weight-threshold* 3]
    (spec)))

(defn filename [file]
  (last (clojure.string/split file #"/")))
