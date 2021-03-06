(ns jukebox-web.test-helper
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :as io]
            [fs.core :as fs]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.artwork :as artwork]
            [jukebox-web.models.playlist :as playlist]))

(def test-db "/tmp/jukebox-test.db")

(defn delete-file
    "Delete file f. Raise an exception if it fails unless silently is true."
    [f & [silently]]
    (or (.delete (io/as-file f))
              silently
              (throw (java.io.IOException. (str "Couldn't delete " f)))))

(defn delete-file-recursively
    "Delete file f. If it's a directory, recursively delete all its contents.
    Raise an exception if any deletion fails unless silently is true."
    [f & [silently]]
    (let [f (io/as-file f)]
          (if (.isDirectory f)
                  (doseq [child (.listFiles f)]
                            (delete-file-recursively child silently)))
          (delete-file f silently)))

(defn with-database-connection [spec]
  (binding [db/*db* {:classname "org.apache.derby.jdbc.EmbeddedDriver"
                     :subprotocol "derby"
                     :subname test-db
                     :create true}]
    (sql/with-connection db/*db*
      (sql/transaction
        (db/migrate!)
        (spec)
        (sql/set-rollback-only)))))

(defn with-test-music-library [spec]
  (binding [library/*music-library* "test/music"]
    (fs/delete-dir "test/music")
    (fs/copy-dir "test/fixtures/music" "test/music")
    (spec)))

(defn with-smaller-weight-threshold [spec]
  (binding [playlist/*weight-threshold* 3]
    (spec)))

(defn filename [file]
  (last (clojure.string/split file #"/")))
