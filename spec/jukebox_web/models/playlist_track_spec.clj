(ns jukebox-web.models.playlist-track-spec
  (:require [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.library :as library])
  (:import [jukebox-web.models.playlist-track PlaylistTrack])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "playlist-track"
  (with-test-music-library)
  (with-database-connection)

  (describe "metadata"
    (it "includes song information, owner, and requester"
        (let [track (PlaylistTrack. (library/file-on-disk "user/artist/album/track.mp3")
                                     {:login "requestinguser"})]
          (should= { :skipCount 0
                     :playCount 0
                     :owner     "user"
                     :requester "requestinguser"
                     :duration  16
                     :title     "jukebox2"
                     :album     "Hammer's Album"
                     :artist    "Hammer"         } (playlist-track/metadata track))))))
