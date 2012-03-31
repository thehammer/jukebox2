(ns jukebox-web.util.file-spec
  (:require [jukebox-web.util.file :as util-file]
            [clojure.java.io :as io])
  (:use [speclj.core]
        [clojure.contrib.seq :only [includes?]]))

(defn file-set [files]
  (set (map io/file files)))

(describe
 "ls"
 (it "returns directories and .mp3 files for a basic directory"
     (should= (file-set ["jukebox2.mp3" "user" "user2"])
              (set (util-file/ls "spec/music" ""))))

 (it "returns relative paths for a directory + path"
     (should= (file-set ["user/artist/album" "user/artist/album2"])
              (set (util-file/ls "spec/music" "user/artist"))))

 (it "returns only files specified by optional filter"
     (should= (file-set ["jukebox2.mp3"])
              (set (util-file/ls "spec/music" "" util-file/mp3?)))))
