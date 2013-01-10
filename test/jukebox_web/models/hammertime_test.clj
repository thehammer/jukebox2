(ns jukebox-web.models.hammertime-test
  (:require [jukebox-web.models.factory :as factory]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.cron :as cron])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-test-music-library with-database-connection)

(deftest create-stores-hammertime
  (hammertime/create! (factory/hammertime {:name "test"}))
  (is (not (nil? (hammertime/find-by-name "test")))))

(deftest delete-deletes-the-hammertime
  (hammertime/create! (factory/hammertime {:name "test"}))
  (let [hammertime (hammertime/find-by-name "test")]
    (hammertime/delete-by-id! (:id hammertime))
    (is (nil? (hammertime/find-by-name "test")))))

(deftest validating-hammertimes
  (testing "requires a name"
    (let [errors (hammertime/validate (factory/hammertime {:name nil}))]
      (is (= ["is required"] (:name errors)))))

  (testing "requires a path"
    (let [errors (hammertime/validate (factory/hammertime {:file nil}))]
      (is (= ["is required"] (:file errors)))))

  (testing "requires a start time"
    (let [errors (hammertime/validate (factory/hammertime {:start nil}))]
      (is (= ["is required"] (:start errors)))))

  (testing "requires a end time"
    (let [errors (hammertime/validate (factory/hammertime {:end nil}))]
      (is (= ["is required"] (:end errors)))))

  (testing "requires a schedule"
    (let [errors (hammertime/validate (factory/hammertime {:schedule nil}))]
      (is (= ["is required"] (:schedule errors))))))

(deftest find-all-returns-all-the-hammertimes
  (hammertime/create! (factory/hammertime))
  (hammertime/create! (factory/hammertime))
  (is (= 2 (count (hammertime/find-all)))))

(deftest find-by-id-returns-nil-when-nothing-found
  (is (nil? (hammertime/find-by-id "random"))))

(deftest find-by-id-returns-the-hammertime
  (let [hammertime (factory/hammertime {:name "some_name"})
        _ (hammertime/create! hammertime)
        id (:id (hammertime/find-by-name "some_name"))]
    (is (= (assoc hammertime :id id) (hammertime/find-by-id id)))))

(deftest update-updates-the-user
  (hammertime/create! (factory/hammertime {:name "test" :start 1}))
  (let [hammertime (hammertime/find-by-name "test")]
    (hammertime/update! hammertime {:start 5})
    (is (= 5 (:start (hammertime/find-by-name "test"))))))

(deftest update-returns-errors-if-validations-fail
  (hammertime/create! (factory/hammertime {:name "test"}))
  (let [hammertime (hammertime/find-by-name "test")
        errors (hammertime/update! hammertime {:start nil})]
    (is (= ["is required"] (:start errors)))))

(deftest schedule-all-does-nothing-with-no-hammertimes
  (hammertime/schedule-all!)
  (is (= 0 (count @cron/*scheduled-tasks*)))
  (is (= [] (cron/scheduled-patterns))))

(deftest schedule-all-tasks
  (hammertime/create! (factory/hammertime {:schedule "1 2 * * *"}))
  (hammertime/create! (factory/hammertime {:schedule "3 4 * * *"}))
  (hammertime/schedule-all!)
  (is (= 2 (count @cron/*scheduled-tasks*)))
  (is (= ["3 4 * * *" "1 2 * * *"] (cron/scheduled-patterns))))
