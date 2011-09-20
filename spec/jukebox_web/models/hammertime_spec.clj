(ns jukebox-web.models.hammertime-spec
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.models.hammertime]
        [jukebox-web.spec-helper]))


(describe "create!"
  (with-database-connection)

  (it "stores the hammertime"
    (create! {:name "test" :file "foo.mp3" :start 4 :end 10})
    (should-not (nil? (find-by-name "test")))))


(describe "validate"
  (it "requires a name"
    (let [errors (validate (factory/hammertime {:name nil}))]
      (should= "is required" (:name errors))))

  (it "requires a path"
    (let [errors (validate (factory/hammertime {:file nil}))]
      (should= "is required" (:file errors))))

  (it "requires a start time"
    (let [errors (validate (factory/hammertime {:start nil}))]
      (should= "is required" (:start errors))))

  (it "requires a end time"
    (let [errors (validate (factory/hammertime {:end nil}))]
      (should= "is required" (:end errors)))))

(describe "find-all"
  (with-database-connection)

  (it "returns all the hammertimes from the database"
    (create! (factory/hammertime {}))
    (create! (factory/hammertime {}))
    (should= 2 (count (find-all)))))
