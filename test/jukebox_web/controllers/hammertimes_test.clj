(ns jukebox-web.controllers.hammertimes-test
  (:require [jukebox-web.controllers.hammertimes :as hammertimes-controller]
            [jukebox-web.models.cron :as cron]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.factory :as factory]
            [clojure.contrib.string :as string])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection)

(deftest saves-valid-hammertimes
  (let [request {:params (factory/hammertime {:name "test"})}
        response (hammertimes-controller/create request)]
    (is (not (nil? (hammertime/find-by-name "test"))))))

(deftest renders-errors-when-not-valid
  (let [request {:params (factory/hammertime {:name nil})}
        response (hammertimes-controller/create request)]
    (is (nil? (:headers response)))
    (is (nil? (hammertime/find-by-name "test")))))

(deftest redirects-to-playlist-after-saving
  (let [request {:params (factory/hammertime {})}
        response (hammertimes-controller/create request)]
    (is (= 302 (:status response)))
    (is (= {"Location" "/playlist"} (:headers response)))))

(deftest reschedules-hammertimes
  (let [request {:params (factory/hammertime {:schedule "1 2 3 4 5"})}
        response (hammertimes-controller/create request)]
    (is (= 302 (:status response)))
    (is (= {"Location" "/playlist"} (:headers response)))
    (is (= ["1 2 3 4 5"] (cron/scheduled-patterns)))))

(deftest delete-deletes-the-hammertime-and-redirects
    (hammertime/create! (factory/hammertime {:name "test"}))
    (let [hammertime (hammertime/find-by-name "test")
          request {:params {:id (:id hammertime)}}
          response (hammertimes-controller/delete request)]
      (is (= 302 (:status response)))
      (is (= {"Location" "/hammertimes"} (:headers response)))
      (is (nil? (hammertime/find-by-name "test")))))

(deftest edit-hammertimes-renders-successfully
  (testing "renders successfully"
    (hammertime/create! (factory/hammertime {:name "test"}))
    (let [hammertime (hammertime/find-by-name "test")
          request {:params {:id (:id hammertime)}}
          response (hammertimes-controller/edit request)]
      (is (string/substring? "Edit Hammertime" response)))))

(deftest update-updates-a-hammertime-and-redirects
  (hammertime/create! (factory/hammertime {:name "test" :start 1}))
  (let [hammertime (hammertime/find-by-name "test")
        request {:params {:id (:id hammertime) :start 5}}
        response (hammertimes-controller/update request)]
    (is (= 302 (:status response)))
    (is (= {"Location" "/hammertimes"} (:headers response)))
    (is (= 5 (:start (hammertime/find-by-name "test"))))))

(deftest update-reschedules-hammertimes
  (hammertime/create! (factory/hammertime {:name "test" :schedule "1 2 3 4 5"}))
  (let [hammertime (hammertime/find-by-name "test")
        request {:params {:id (:id hammertime) :schedule "5 4 3 2 1"}}
        response (hammertimes-controller/update request)]
    (is (= 302 (:status response)))
    (is (= ["5 4 3 2 1"] (cron/scheduled-patterns)))))
