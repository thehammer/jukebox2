(ns jukebox-web.models.track-search-spec
  (:require [jukebox-web.models.track-search :as track-search]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "execute"
  (with-test-music-library)

  (it "searches the file system for tracks"
      (should= "jukebox2.mp3" (filename (first (track-search/execute "jukebox")))))

  (it "searches file system for artist and returns all tracks"
      (let [tracks (track-search/execute "artist")]
        (should= 4 (count tracks))
        (should= "track.mp3" (filename (first tracks)))))

  (it "searches file system for album and returns all tracks"
      (let [tracks (track-search/execute "album2")]
        (should= 1 (count tracks))
        (should= "track.mp3" (filename (first tracks)))))

  (it "doesn't find a matching track"
      (should-be-nil (first (track-search/execute "lovesong")))))
