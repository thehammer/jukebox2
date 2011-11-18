(ns jukebox-web.models.library-spec
  (:require [clojure.java.io :as io]
            [jukebox-web.models.library :as library])
  (:use [speclj.core]
        [clojure.contrib.seq :only [includes?]]
        [jukebox-web.spec-helper]))

(describe "library"
  (with-test-music-library)

  (describe "list-directory"
    (it "returns a seq with the files contained in the root path with no arguments"
      (let [files (map #(.getPath %) (library/list-directory))]
        (should-not (includes? files "jukebox2.ogg"))
        (should (includes? files "jukebox2.mp3"))))

    (it "returns the list of files for a given directory"
      (let [files (map #(.getPath %) (library/list-directory "user"))]
        (should (includes? files "user/artist"))))

    (it "does not include dotfiles in the list"
      (let [files (map #(.getPath %) (library/list-directory))]
        (should-not (includes? files ".gitkeep")))))

  (describe "parent-directory"
    (it "returns nil when path is the music library"
      (should= nil (library/parent-directory "")))

    (it "returns the root when its the user"
      (should= "" (library/parent-directory "user")))

    (it "returns the user when in the artist directory"
      (should= "user" (library/parent-directory "user/artist")))

    (it "returns url encoded values"
      (should= "daft+punk" (library/parent-directory "daft punk/discovery")))

    (it "returns url encoded values correctly for subdirectories"
      (should= "user%2Fdaft+punk" (library/parent-directory "user/daft punk/discovery"))))

  (describe "all-tracks"
    (it "only returns files matching .mp3"
      (let [tracks (library/all-tracks)]
        (should-not (includes? (map #(.getName %) tracks) "jukebox2.ogg"))))

    (it "does not return dotfiles"
      (let [tracks (library/all-tracks)]
        (should-not (includes? (map #(.getName %) tracks) ".gitkeep"))))

    (it "allows scoping by subdirectory"
      (let [tracks (library/all-tracks "user")]
        (should (includes? (map #(.getName %) tracks) "track.mp3"))
        (should-not (includes? (map #(.getName %) tracks) "jukebox.mp3")))))

  (describe "random-song"
    (it "returns a random song with no prefix when no argument is provided"
      (should-not-be-nil (library/random-song)))

    (it "returns a random song from the given path"
      (let [selections (take 10 (map library/random-song (repeat "user")))]
        (should-not (includes? (map #(.getName %) selections) "jukebox.mp3")))))

  (describe "owner"
    (it "returns nil for a path without a user"
      (let [track (io/file library/*music-library* "jukebox2.mp3")]
        (should-be-nil (library/owner track))))

    (it "returns the login for a path with a user"
      (let [track (io/file library/*music-library* "user/artist/album/jukebox2.mp3")]
        (should= "user" (library/owner track)))))

  (describe "play-count"
    (with-database-connection)

    (it "returns 0 for a new track"
      (should= 0 (library/play-count (library/random-song))))

    (it "increments with increment-play-count!"
      (let [track (library/random-song)]
        (library/increment-play-count! track)
        (should= 1 (library/play-count track))
        (library/increment-play-count! track)
        (should= 2 (library/play-count track)))))

  (describe "most-played"
    (with-database-connection)

    (it "returns the most-played songs in descending order"
      (dotimes [_ 3] (library/increment-play-count! "three"))
      (dotimes [_ 1] (library/increment-play-count! "one"))
      (dotimes [_ 2] (library/increment-play-count! "two"))
      (let [most-played (library/most-played)]
        (should= 3 (count most-played))
        (should= {:track "three" :count 3} (nth most-played 0))
        (should= {:track "two" :count 2} (nth most-played 1))
        (should= {:track "one" :count 1} (nth most-played 2))))

    (it "limits the number of tracks to 20"
      (dotimes [n 21] (library/increment-play-count! n))
      (should= 20 (count (library/most-played))))))
