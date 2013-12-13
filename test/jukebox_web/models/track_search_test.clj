(ns jukebox-web.models.track-search-test
  (:require [jukebox-web.models.track-search :as track-search]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.factory :as factory])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-test-music-library)

(deftest searching
  (testing "searches the file system for tracks"
    (is (= #{"jukebox2.m4a", "jukebox2.mp3"}
           (set (map filename (track-search/execute "jukebox"))))))

  (testing "searches the file system for tracks with special characters"
    (is (= #{"$pecial.mp3"}
           (set (map filename (track-search/execute "$pecial"))))))

  (testing "searches file system for artist and returns all tracks"
    (let [tracks (track-search/execute "artist")]
      (is (= 4 (count tracks)))))

  (testing "searches file system for album and returns all tracks"
    (let [tracks (track-search/execute "album2")]
      (is (= 1 (count tracks)))
      (is (= "track.mp3" (filename (first tracks))))))

  (testing "doesn't find a matching track"
    (is (nil? (first (track-search/execute "lovesong"))))))
