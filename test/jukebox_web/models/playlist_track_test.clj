(ns jukebox-web.models.playlist-track-test
  (:require [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.library :as library])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each
              with-test-music-library
              with-database-connection
              (fn [f]
                (user/sign-up! (factory/user {:login "user"}))
                (user/sign-up! (factory/user {:login "user2"}))
                (f)))

(deftest new-playlist-track-caches-artwork
  (let [track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3")
                                                 {:login "user"}
                                                 "")]
    (is (not (nil? (:artwork track))))))


(deftest metadata-includes-song-information-owner-and-requester
  (let [user (user/find-by-login "user")
        track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3")
                                                 {:login "user"}
                                                 "")]
    (is (= {:skipCount   0
            :playCount   0
            :owner       "user"
            :requester   "user"
            :duration    16
            :isRequester true
            :id          ""
            :progress    0
            :artwork     "no_art_lrg.png"
            :playing     false
            :title       "jukebox2"
            :album       "Hammer's Album"
            :artist      "Hammer"}
           (playlist-track/metadata track user)))))
