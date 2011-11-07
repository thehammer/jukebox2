(ns jukebox-web.controllers.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.library :as library]
            [jukebox-web.controllers.playlist :as playlist-controller])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "add"
  (before (playlist/reset-state!))

  (it "adds the given file to the end of the queued-songs"
    (let [song "user/artist/album/track.mp3"
          request {:params {:song song}}
          response (playlist-controller/add request)]
      (should= (library/file-on-disk song) (first (playlist/queued-songs))))))
