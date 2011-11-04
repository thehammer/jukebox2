(ns jukebox-web.models.hammertime-spec
  (:require [jukebox-web.models.factory :as factory]
            [jukebox-web.models.hammertime :as hammertime])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "hammertime"
  (with-test-music-library)
  (with-database-connection)

  (describe "hammertime/create!"
    (it "stores the hammertime"
      (hammertime/create! (factory/hammertime {:name "test"}))
      (should-not-be-nil (hammertime/find-by-name "test"))))

  (describe "hammertime/delete-by-id!"
    (it "deletes the hammertime"
      (hammertime/create! (factory/hammertime {:name "test"}))
      (let [hammertime (hammertime/find-by-name "test")]
        (hammertime/delete-by-id! (:id hammertime))
        (should-be-nil (hammertime/find-by-name "test")))))

  (describe "hammertime/validate"
    (it "requires a name"
      (let [errors (hammertime/validate (factory/hammertime {:name nil}))]
        (should= ["is required"] (:name errors))))

    (it "requires a path"
      (let [errors (hammertime/validate (factory/hammertime {:file nil}))]
        (should= ["is required"] (:file errors))))

    (it "requires a start time"
      (let [errors (hammertime/validate (factory/hammertime {:start nil}))]
        (should= ["is required"] (:start errors))))

    (it "requires a end time"
      (let [errors (hammertime/validate (factory/hammertime {:end nil}))]
        (should= ["is required"] (:end errors))))

    (it "requires a schedule"
      (let [errors (hammertime/validate (factory/hammertime {:schedule nil}))]
        (should= ["is required"] (:schedule errors)))))

  (describe "find-all"
    (it "returns all the hammertimes from the database"
      (hammertime/create! (factory/hammertime))
      (hammertime/create! (factory/hammertime))
      (should= 2 (count (hammertime/find-all)))))

  (describe "find-by-id"
    (it "returns nil when no results"
      (should-be-nil (hammertime/find-by-id "random")))

    (it "returns a hammertime by id"
      (let [hammertime (factory/hammertime {:name "some_name"})
            _ (hammertime/create! hammertime)
            id (:id (hammertime/find-by-name "some_name"))]
        (should= (assoc hammertime :id id) (hammertime/find-by-id id)))))

  (describe "hammertime/update!"
    (it "updates the user"
      (hammertime/create! (factory/hammertime {:name "test" :start 1}))
      (let [hammertime (hammertime/find-by-name "test")]
        (hammertime/update! hammertime {:start 5})
        (should= 5 (:start (hammertime/find-by-name "test")))))

    (it "returns errors if validations fail"
      (hammertime/create! (factory/hammertime {:name "test"}))
      (let [hammertime (hammertime/find-by-name "test")
            errors (hammertime/update! hammertime {:start nil})]
        (should= ["is required"] (:start errors)))))

  (describe "schedule-all!"
    (it "scheduled nothing if there are no hammertimes"
      (should= 0 (count @hammertime/*scheduled-tasks*))
      (hammertime/schedule-all!)
      (should= 0 (count @hammertime/*scheduled-tasks*))
      (should= [] (scheduled-cron-patterns)))

    (it "schedules all tasks"
      (hammertime/create! (factory/hammertime {:schedule "1 2 * * *"}))
      (hammertime/create! (factory/hammertime {:schedule "3 4 * * *"}))
      (hammertime/schedule-all!)
      (should= 2 (count @hammertime/*scheduled-tasks*))
      (should= ["3 4 * * *" "1 2 * * *"] (scheduled-cron-patterns)))))
