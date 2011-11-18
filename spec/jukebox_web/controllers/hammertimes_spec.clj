(ns jukebox-web.controllers.player-spec
  (:require [jukebox-web.controllers.hammertimes :as hammertimes-controller]
            [jukebox-web.models.cron :as cron]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.factory :as factory]
            [clojure.contrib.string :as string])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "hammertimes"
  (with-database-connection)

  (describe "create"
    (it "saves valid hammertimes"
      (let [request {:params (factory/hammertime {:name "test"})}
            response (hammertimes-controller/create request)]
        (should-not-be-nil (hammertime/find-by-name "test"))))

    (it "renders errors when not valid"
      (let [request {:params (factory/hammertime {:name nil})}
            response (hammertimes-controller/create request)]
        (should-be-nil (:headers response))
        (should-be-nil (hammertime/find-by-name "test"))))

    (it "redirects to playlist after saving"
      (let [request {:params (factory/hammertime {})}
            response (hammertimes-controller/create request)]
        (should= 302 (:status response))
        (should= {"Location" "/playlist"} (:headers response))))

    (it "reschedules hammertimes"
      (let [request {:params (factory/hammertime {:schedule "1 2 3 4 5"})}
            response (hammertimes-controller/create request)]
        (should= 302 (:status response))
        (should= {"Location" "/playlist"} (:headers response))
        (should= ["1 2 3 4 5"] (cron/scheduled-patterns)))))

  (describe "delete"
    (it "deletes the hammertime and redirects"
      (hammertime/create! (factory/hammertime {:name "test"}))
      (let [hammertime (hammertime/find-by-name "test")
            request {:params {:id (:id hammertime)}}
            response (hammertimes-controller/delete request)]
        (should= 302 (:status response))
        (should= {"Location" "/hammertimes"} (:headers response))
        (should-be-nil (hammertime/find-by-name "test")))))

  (describe "edit"
    (it "renders successfully"
      (hammertime/create! (factory/hammertime {:name "test"}))
      (let [hammertime (hammertime/find-by-name "test")
            request {:params {:id (:id hammertime)}}
            response (hammertimes-controller/edit request)]
        (should (string/substring? "Edit Hammertime" response)))))

  (describe "update"
    (it "updates a hammertime and redirects"
      (hammertime/create! (factory/hammertime {:name "test" :start 1}))
      (let [hammertime (hammertime/find-by-name "test")
            request {:params {:id (:id hammertime) :start 5}}
            response (hammertimes-controller/update request)]
        (should= 302 (:status response))
        (should= {"Location" "/hammertimes"} (:headers response))
        (should= 5 (:start (hammertime/find-by-name "test")))))

    (it "reschedules hammertimes"
      (hammertime/create! (factory/hammertime {:name "test" :schedule "1 2 3 4 5"}))
      (let [hammertime (hammertime/find-by-name "test")
            request {:params {:id (:id hammertime) :schedule "5 4 3 2 1"}}
            response (hammertimes-controller/update request)]
        (should= 302 (:status response))
        (should= ["5 4 3 2 1"] (cron/scheduled-patterns))))))
