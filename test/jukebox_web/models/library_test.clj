(ns jukebox-web.models.library-test
  (:require [clojure.java.io :as io]
            [jukebox-web.models.library :as library])
  (:use [clojure.test]
        [clojure.contrib.seq :only [includes?]]
        [jukebox-web.test-helper]))

(use-fixtures :each with-test-music-library with-database-connection)

(deftest listing-a-directory
  (testing "returns a seq with the files contained in the root path with no arguments"
    (let [files (map #(.getPath %) (library/list-directory))]
      (is (not (includes? files "jukebox2.ogg")))
      (is (includes? files "jukebox2.mp3"))))

  (testing "returns the list of files for a given directory"
    (let [files (map #(.getPath %) (library/list-directory "user"))]
      (is (includes? files "user/artist"))))

  (testing "does not include dotfiles in the list"
    (let [files (map #(.getPath %) (library/list-directory))]
      (is (not (includes? files ".gitkeep"))))))

(deftest finding-parent-directory
  (testing "returns nil when path is the music library"
    (is (= nil (library/parent-directory ""))))

  (testing "returns the root when its the user"
    (is (= "" (library/parent-directory "user"))))

  (testing "returns the user when in the artist directory"
    (is (= "user" (library/parent-directory "user/artist"))))

  (testing "returns url encoded values"
    (is (= "daft+punk" (library/parent-directory "daft punk/discovery"))))

  (testing "returns url encoded values correctly for subdirectories"
    (is (= "user%2Fdaft+punk" (library/parent-directory "user/daft punk/discovery")))))

(deftest finding-all-tracks
  (testing "returns files matching .mp3 and m4a"
    (let [tracks (library/all-tracks)]
      (is (not (includes? (map #(.getName %) tracks) "jukebox2.ogg")))
      (is (includes? (map #(.getName %) tracks) "jukebox2.m4a"))
      (is (includes? (map #(.getName %) tracks) "jukebox2.mp3"))))

  (testing "does not return dotfiles"
    (let [tracks (library/all-tracks)]
      (is (not (includes? (map #(.getName %) tracks) ".gitkeep")))))

  (testing "allows scoping by subdirectory"
    (let [tracks (library/all-tracks "user")]
      (is (includes? (map #(.getName %) tracks) "track.mp3"))
      (is (not (includes? (map #(.getName %) tracks) "jukebox.mp3"))))))

(deftest find-random-song
  (testing "returns a random song with no prefix when no argument is provided"
    (is (not (nil? (library/random-song)))))

  (testing "returns a random song from the given path"
    (let [selections (take 10 (map library/random-song (repeat "user")))]
      (is (not (includes? (map #(.getName %) selections) "jukebox.mp3"))))))

(deftest owners
  (testing "returns nil for a path without a user"
    (let [track (io/file library/*music-library* "jukebox2.mp3")]
      (is (nil? (library/owner track)))))

  (testing "returns the login for a path with a user"
    (let [track (io/file library/*music-library* "user/artist/album/jukebox2.mp3")]
      (is (= "user" (library/owner track))))))

(deftest play-count-returns-0-for-a-new-track
  (is (= 0 (library/play-count (library/random-song)))))

(deftest play-count-is-incremented
  (let [track (library/random-song)]
    (library/increment-play-count! track)
    (is (= 1 (library/play-count track)))
    (library/increment-play-count! track)
    (is (= 2 (library/play-count track)))))

(deftest skip-count-return-zero-for-new-track
  (is (= 0 (library/skip-count (library/random-song)))))

(deftest can-increment-skip-count
  (let [track (library/random-song)]
    (library/increment-skip-count! track)
    (is (= 1 (library/skip-count track)))
    (library/increment-skip-count! track)
    (is (= 2 (library/skip-count track)))))

(deftest can-return-most-played-songs-in-descending-order
  (dotimes [_ 3] (library/increment-play-count! "three"))
  (dotimes [_ 1] (library/increment-play-count! "one"))
  (dotimes [_ 2] (library/increment-play-count! "two"))
  (let [most-played (library/most-played)]
    (is (= 3 (count most-played)))
    (is (= "three" (:track (nth most-played 0))))
    (is (= "two"   (:track (nth most-played 1))))
    (is (= "one"   (:track (nth most-played 2))))))

(deftest returns-only-the-20-most-played-tracks
  (dotimes [n 21] (library/increment-play-count! n))
  (is (= 20 (count (library/most-played)))))

(deftest finding-artists
  (testing "returns nil for a path without an artist"
    (let [track (io/file library/*music-library* "jukebox2.mp3")]
      (is (nil? (library/artist track)))))

  (testing "returns the artist for a path with an artist"
    (let [track (io/file library/*music-library* "user/artist/album/jukebox2.mp3")]
      (is (= "artist" (library/artist track))))))

(deftest finds-most-popular-artists
  (testing "returns an array with the artist and number of tracks in descending order"
    (is (= [["artist" 3] ["artist2" 1]] (library/most-popular-artists)))))
