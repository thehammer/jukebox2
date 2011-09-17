(ns jukebox-web.spec-helper
  (:use [fleetdb.embedded :as fleetdb]
        [jukebox-web.models.db :as db]))

(defn with-database-connection [spec]
  (let [connection (fleetdb/init-ephemeral)]
    (binding [db/*db* connection]
      (spec))
    (fleetdb/close connection)))
