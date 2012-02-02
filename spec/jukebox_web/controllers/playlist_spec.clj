(ns jukebox-web.controllers.playlist-spec
  (:require [clj-json.core :as json]
            [clojure.contrib.pprint :as pprint]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.library :as library]
            [jukebox-web.controllers.playlist :as playlist-controller])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "add"
  (with-database-connection)

  (before (playlist/reset-state!))

  (it "doesn't add a song if the user isn't logged in"
    (let [song "user/artist/album/track.mp3"
          request {:params {:song song} :headers {"accept" "text/html"}}
          response (playlist-controller/add request)]
      (should= [] (playlist/queued-songs))))

  (it "adds the given file to the end of the queued-songs if user is logged in"
    (user/sign-up! (factory/user {:login "user"}))
    (let [song "user/artist/album/track.mp3"
          request {:params {:song song} :headers {"accept" "text/html"} :session {:current-user "user"}}
          response (playlist-controller/add request)]
      (should= (library/file-on-disk song) (:song (first (playlist/queued-songs)))))))

(describe "delete"
  (with-test-music-library)
  (with-database-connection)
  (before (playlist/reset-state!))

  (it "deletes a song from the queue if requesters match"
    (user/sign-up! (factory/user {:login "user"}))
    (playlist/add-song! "user/artist/album/track.mp3" {:login "user"})
    (let [uuid (:id (first (playlist/queued-songs)))
          request {:params {:id uuid} :headers {"accept" "application/json"} :session {:current-user "user"}}
          response (playlist-controller/delete request)]
      (should= [] (playlist/queued-songs))))

  (it "doesn't deletes a song from the queue if user isn't requester"
    (user/sign-up! (factory/user {:login "user"}))
    (playlist/add-song! "user/artist/album/track.mp3" {:login "user2"})
    (let [uuid (:id (first (playlist/queued-songs)))
          request {:params {:id uuid} :headers {"accept" "application/json"} :session {:current-user "user"}}
          response (playlist-controller/delete request)]
      (should= 1 (count (playlist/queued-songs))))))

(describe "index"
  (with-test-music-library)
  (with-database-connection)

  (before
    (playlist/reset-state!)
    (playlist/add-song! "user/artist/album/track.mp3")
    (playlist/add-song! "user/artist/album/track2.mp3"))

  (it "yields the current playlist as a json response"
    (let [request {:headers {"accept" "application/json"}}
          uuid (:id (first (playlist/queued-songs)))
          response (playlist-controller/index request)
          response-json (json/parse-string (:body response))]
      (should= 2 (count response-json))
      (should= { "playCount" 0
                 "skipCount" 0
                 "owner"     "user"
                 "requester" nil
                 "duration"  16
                 "title"     "jukebox2"
                 "progress"  0
                 "playing"   false
                 "isRequester"   false
                 "id"   uuid
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
          uuid (:id (playlist/current-song))
          response (playlist-controller/current-track request)
          response-json (json/parse-string (:body response))]
      (should= { "playCount" 1
                 "skipCount" 0
                 "owner"     "user"
                 "requester" nil
                 "duration"  16
                 "title"     "jukebox2"
                 "progress"  0
                 "playing"   false
                 "isRequester"   false
                 "id" uuid
                 "album"     "Hammer's Album"
                 "artist"    "Hammer"         } response-json)
      )))


