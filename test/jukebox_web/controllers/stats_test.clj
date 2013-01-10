(ns jukebox-web.controllers.stats-test
  (:require [jukebox-web.controllers.stats :as stats-controller]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.user :as user]
            [clojure.contrib.string :as string]
            [clj-json.core :as json])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest index-show-stats
  (testing "renders successfully"
    (let [response (stats-controller/index nil)]
      (is (string/substring? "Stats" response))
      (is (string/substring? "Most Played Tracks" response))
      (is (string/substring? "Most Popular Artists" response)))))

(deftest shows-song-counts
  (testing "returns an array of users and song counts"
    (user/sign-up! (factory/user {:login "user"}))
    (user/sign-up! (factory/user {:login "user2"}))
    (user/sign-up! (factory/user {:login "user3"}))
    (let [response (stats-controller/song-counts nil)]
      (is (= [["user" 3] ["user2" 1] ["user3" 0]] (json/parse-string (:body response)))))))
