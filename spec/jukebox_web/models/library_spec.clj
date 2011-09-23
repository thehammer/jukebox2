(ns jukebox-web.models.library-spec
  (:require [jukebox-web.models.library :as library])
  (:use [speclj.core]
        [clojure.contrib.seq :only [includes?]]))

(describe "library"
  (describe "list-directory"
    (it "returns a seq with the files contained in the root path with no arguments"
      (let [files (map #(.getPath %) (library/list-directory))]
        (should (includes? files "jukebox2.mp3"))))

    (it "returns the list of files for a given directory"
      (let [files (map #(.getPath %) (library/list-directory "user"))]
        (should (includes? files "user/artist"))))

    (it "does not include dotfiles in the list"
      (let [files (map #(.getPath %) (library/list-directory))]
        (should-not (includes? files ".gitkeep"))))))
