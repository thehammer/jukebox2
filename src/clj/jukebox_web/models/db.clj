(ns jukebox-web.models.db
  (:import [java.io File])
  (:require [clojure.java.jdbc :as sql]))

(def ^:dynamic *db*)

(defn connect! [db]
  (defonce ^:dynamic *db* db))

(defn keys-to-keywords [record]
  (reduce #(conj %1 [(keyword (first %2)) (nth %2 1)]) {} record))


(defn migrate! []
  (let [tables (resultset-seq (-> (sql/connection)
                                  (.getMetaData)
                                  (.getTables nil "APP" "%", nil)))]

    (when (empty? (filter #(= "TRACK_METADATA" (:table_name %)) tables))
      (sql/create-table
        :track_metadata
        [:id "INTEGER" "NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"]
        [:album "VARCHAR(255)"]
        [:artist "VARCHAR(255)"]
        [:title "VARCHAR(255)"]
        [:location "VARCHAR(255)" "NOT NULL"]
        [:tempfile_location "VARCHAR(255)"]
        [:skip_count "INTEGER" "DEFAULT 0"]
        [:play_count "INTEGER" "DEFAULT 0"]))

    (when (empty? (filter #(= "PLAY_COUNTS" (:table_name %)) tables))
      (sql/create-table
        :play_counts
        [:id "INTEGER" "NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"]
        [:track "VARCHAR(255)" "NOT NULL"]
        [:count "INTEGER" "DEFAULT 0"]))

    (when (empty? (filter #(= "SKIP_COUNTS" (:table_name %)) tables))
      (sql/create-table
        :skip_counts
        [:id "INTEGER" "NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"]
        [:track "VARCHAR(255)" "NOT NULL"]
        [:count "INTEGER" "DEFAULT 0"]))

    (when (empty? (filter #(= "USERS" (:table_name %)) tables))
      (sql/create-table
        :users
        [:id "INTEGER" "NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"]
        [:login "VARCHAR(255)" "NOT NULL"]
        [:skip_count "INTEGER" "DEFAULT 0"]
        [:password "VARCHAR(255)"]
        [:avatar "VARCHAR(255)"]
        [:enabled "BOOLEAN" "DEFAULT TRUE"]))))

(defn delete [model id]
  (sql/delete-rows model ["id=?" id]))

(defn find-all [query-with-bindings]
  (sql/with-query-results result-seq query-with-bindings
    (if-let [results (doall result-seq)]
      results
      [])))

(defn insert [model record]
  (sql/insert-records model record)
  (sql/with-query-results res ["VALUES IDENTITY_VAL_LOCAL()"]
    (assoc record :id (int (first (vals (first res)))))))

(defn find-by-field [model field value]
  (find-all [(str "SELECT * "
                  "FROM " (name model) " "
                  "WHERE " (name field) " = ?") value]))

(defn update [model updates field value]
  (sql/update-values model
                     [(str (name field) "=?") value]
                     updates))
