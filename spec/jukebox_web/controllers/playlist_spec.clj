(ns jukebox-web.controllers.playlist-spec
  (:require [clj-json.core :as json]
            [clojure.contrib.pprint :as pprint]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.library :as library]
            [jukebox-web.controllers.playlist :as playlist-controller])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "add"
  (with-database-connection)

  (before (playlist/reset-state!))

  (it "adds the given file to the end of the queued-songs"
    (let [song "user/artist/album/track.mp3"
          request {:params {:song song} :headers {"accept" "text/html"}}
          response (playlist-controller/add request)]
      (should= (library/file-on-disk song) (:song (first (playlist/queued-songs)))))))

(describe "index"
  (with-test-music-library)
  (with-database-connection)

  (before
    (playlist/reset-state!)
    (playlist/add-song! "user/artist/album/track.mp3")
    (playlist/add-song! "user/artist/album/track2.mp3"))

  (it "yields the current playlist as a json response"
    (let [request {:headers {"accept" "application/json"}}
          response (playlist-controller/index request)
          response-json (json/parse-string (:body response))]
      (should= 2 (count response-json))
      (should= { "playCount" 0
                 "owner"     "user"
                 "requester" nil
                 "duration"  16
                 "title"     "jukebox2"
                 "album"     "Hammer's Album"
                 "artist"    "Hammer"         } (first response-json))
      )))

(describe "current-track"
  (with-test-music-library)
  (with-database-connection)

  (before
    (playlist/reset-state!)
    (playlist/add-song! "user/artist/album/track.mp3")
    (playlist/next-track nil))

  (it "yields the current track as a json response"
    (let [request {:headers {"accept" "application/json"}}
          response (playlist-controller/current-track request)
          response-json (json/parse-string (:body response))]
      (should= { "playCount" 1
                 "owner"     "user"
                 "requester" nil
                 "duration"  16
                 "title"     "jukebox2"
                 "progress"  0
                 "playing"   false
                 "canSkip"   false
                 "album"     "Hammer's Album"
                 "artist"    "Hammer"         } response-json)
      )))


