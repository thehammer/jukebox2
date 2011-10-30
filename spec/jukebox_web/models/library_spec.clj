(ns jukebox-web.models.library-spec
  (:require [jukebox-web.models.library :as library])
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

  (describe "owner"
    (it "returns nil for a path without a user"
      (should-be-nil (library/owner (java.io.File. "music/jukebox.mp3"))))

    (it "returns nil for a nil argument"
      (should-be-nil (library/owner nil)))

    (it "returns the login for a path with a user"
      (should= "user" (library/owner (java.io.File. "music/user/artist/album/track.jukeboxmp3"))))))
