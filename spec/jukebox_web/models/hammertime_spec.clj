(ns jukebox-web.models.hammertime-spec
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.models.hammertime]
        [jukebox-web.spec-helper]))

(describe "hammertime"
  (with-test-music-library)
  (with-database-connection)

  (describe "create!"
    (it "stores the hammertime"
      (create! {:name "test" :file "foo.mp3" :start 4 :end 10})
      (should-not-be-nil (find-by-name "test"))))

  (describe "delete-by-id!"
    (it "deletes the hammertime"
      (create! {:name "test" :file "foo.mp3" :start 4 :end 10})
      (let [hammertime (find-by-name "test")]
        (delete-by-id! (:id hammertime))
        (should-be-nil (find-by-name "test")))))

  (describe "validate"
    (it "requires a name"
      (let [errors (validate (factory/hammertime {:name nil}))]
        (should= ["is required"] (:name errors))))

    (it "requires a path"
      (let [errors (validate (factory/hammertime {:file nil}))]
        (should= ["is required"] (:file errors))))

    (it "requires a start time"
      (let [errors (validate (factory/hammertime {:start nil}))]
        (should= ["is required"] (:start errors))))

    (it "requires a end time"
      (let [errors (validate (factory/hammertime {:end nil}))]
        (should= ["is required"] (:end errors)))))

  (describe "find-all"
    (it "returns all the hammertimes from the database"
      (create! (factory/hammertime))
      (create! (factory/hammertime))
      (should= 2 (count (find-all)))))

  (describe "find-by-id"
    (it "returns nil when no results"
      (should-be-nil (find-by-id "random")))

    (it "returns a hammertime by id"
      (let [hammertime (factory/hammertime {:name "some_name"})
            _ (create! hammertime)
            id (:id (find-by-name "some_name"))]
        (should= (assoc hammertime :id id) (find-by-id id)))))

  (describe "update!"
    (it "updates the user"
      (create! (factory/hammertime {:name "test" :start 1}))
      (let [hammertime (find-by-name "test")]
        (update! hammertime {:start 5})
        (should= 5 (:start (find-by-name "test")))))

    (it "returns errors if validations fail"
      (create! (factory/hammertime {:name "test"}))
      (let [hammertime (find-by-name "test")
            errors (update! hammertime {:start nil})]
        (should= ["is required"] (:start errors))))))
