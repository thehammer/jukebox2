(ns jukebox-web.models.db
  (:import [java.util UUID])
  (:use [fleetdb.embedded :as fleetdb]
        [clojure.contrib.string :only [as-str]]))

(def *db* :no-database-connection)

(defn- create-pk [record]
  (conj record ["id" (str ( UUID/randomUUID))]))

(defn- keys-to-strings [record]
  (reduce #(conj %1 [(as-str (first %2)) (nth %2 1)]) {} record))

(defn- keys-to-keywords [record]
  (reduce #(conj %1 [(keyword (first %2)) (nth %2 1)]) {} record))

(defn insert [model record]
  (fleetdb/query *db* ["insert" model (create-pk (keys-to-strings record))]))

(defn find-by-field [model field value]
  (map keys-to-keywords (fleetdb/query *db* ["select" model {"where" ["=" field value]}])))
