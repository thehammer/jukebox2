(ns jukebox-web.models.track-search-spec
  (:require [jukebox-web.models.track-search :as track-search]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "execute"
  (with-test-music-library)

  (it "searches the file system for matches and returns tracks"
      (should= "jukebox2.mp3" (filename (first (track-search/execute "jukebox")))))

  (it "doesn't find a matching track"
      (should-be-nil (first (track-search/execute "lovesong")))))
