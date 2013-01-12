(ns jukebox-web.models.db-test
  (:require [clojure.contrib.sql :as sql]
            [jukebox-web.test-helper :as helper])
  (:use clojure.test
        jukebox-web.models.db))

(defn with-test-schema [f]
  (sql/create-table
    :people
    [:id "INTEGER" "NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"]
    [:name "VARCHAR(255)" "NOT NULL"])
  (f))

(use-fixtures :each helper/with-database-connection with-test-schema)

(deftest can-insert-and-find-records
  (insert :people {:name "Hammer"})
  (is (= "Hammer" (:name (first (find-all ["select * from people"]))))))

(deftest can-delete-records
  (let [{:keys [id]} (insert :people {:name "Hammer"})]
    (delete :people id))
  (is (= [] (find-all ["select * from people"]))))

(deftest can-update-records
  (let [{:keys [id]} (insert :people {:name "Hammer"})]
    (update :people {:name "Jason"} :id id))
  (is (= "Jason" (:name (first (find-all ["select * from people"]))))))

(deftest can-find-records-by-arbitrary-sql
  (let [{:keys [id]} (insert :people {:name "Hammer"})]
    (is (= [{:id id}] (find-all ["SELECT id FROM people WHERE name = ?" "Hammer"])))))

(deftest can-find-by-field
  (let [{:keys [id]} (insert :people {:name "Hammer"})]
    (insert :people {:name "Tony"})
    (is (= [{:id id :name "Hammer"}]
           (find-by-field :people :name "Hammer")))))
