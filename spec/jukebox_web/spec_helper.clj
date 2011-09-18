(ns jukebox-web.spec-helper
  (:use [fleetdb.embedded :as fleetdb]
        [jukebox-web.models.db :as db]
        [speclj.core]))

(defn with-database-connection []
  (around [spec]
    (let [connection (fleetdb/init-ephemeral)]
      (binding [db/*db* connection]
        (spec))
      (fleetdb/close connection))))
