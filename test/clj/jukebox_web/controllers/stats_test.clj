(ns jukebox-web.controllers.stats-test
  (:require [jukebox-web.controllers.stats :as stats-controller]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.user :as user]
            [cheshire.core :as json])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest index-show-stats
  (testing "renders successfully"
    (let [response (stats-controller/index nil)]
      (is (.contains response "Stats"))
      (is (.contains response "Most Played Tracks"))
      (is (.contains response "Most Popular Artists")))))

(deftest shows-song-counts
  (testing "returns an array of users and song counts"
    (user/sign-up! (factory/user {:login "user"}))
    (user/sign-up! (factory/user {:login "user2"}))
    (user/sign-up! (factory/user {:login "user3"}))
    (let [response (stats-controller/song-counts nil)]
      (is (= [["user" 3] ["user2" 1] ["user3" 0]] (json/parse-string (:body response)))))))
