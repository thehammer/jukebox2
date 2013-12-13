(ns jukebox-web.util.file-test
  (:require [jukebox-web.util.file :as util-file]
            [clojure.java.io :as io])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(defn file-set [files]
  (set (map io/file files)))

(deftest list-files
  (use-fixtures :each with-test-music-library)

  (testing "returns directories and .mp3 files for a basic directory"
    (is (= (file-set ["jukebox2.m4a" "jukebox2.mp3" "$pecial.mp3" "user" "user2"])
           (set (util-file/ls "test/music" "")))))

  (testing "returns relative paths for a directory + path"
    (is (= (file-set ["user/artist/album" "user/artist/album2"])
           (set (util-file/ls "test/music" "user/artist")))))

  (testing "returns only known files(mp3 and m4a)"
    (is (= (file-set ["jukebox2.m4a", "$pecial.mp3", "jukebox2.mp3"])
           (set (util-file/ls "test/music" "" util-file/has-known-extension?))))))
