(ns jukebox-web.controllers.playlist-test
  (:require [cheshire.core :as json]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.library :as library]
            [jukebox-web.controllers.playlist :as playlist-controller])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(defn reset-playlist [f]
  (playlist/reset-state!)
  (f))

(use-fixtures :each with-database-connection with-test-music-library reset-playlist)

(deftest add-track-returns-forbidden-if-user-not-logged-in
  (let [request {:params {:track-id 1} :session {}}
        response (playlist-controller/add-track request)]
    (is (= 403 (:status response)))))

(deftest add-track-adds-the-given-track-to-the-playlist
  (let [request {:params {:track-id 1} :session {:current-user "user"}}
        response (playlist-controller/add-track request)]
    (is (= 200 (:status response)))
    (is (= 1 (:id (first (playlist/queued-songs)))))))



;(deftest add-tracks-doesnt-add-a-song-if-the-user-isnt-logged-in
;  (let [song "user/artist/album/track.mp3"
;        request {:params {:song song} :headers {"accept" "text/html"}}
;        response (playlist-controller/add request)]
;    (is (= [] (playlist/queued-songs)))))
;
;(deftest add-tracks-adds-the-given-file-to-the-end-of-the-queued-songs-if-user-is-logged-in
;  (user/sign-up! (factory/user {:login "user"}))
;  (let [song "user/artist/album/track.mp3"
;        request {:params {:song song} :headers {"accept" "text/html"} :session {:current-user "user"}}
;        response (playlist-controller/add request)]
;    (is (= (library/file-on-disk song) (:song (first (playlist/queued-songs)))))))
;
;
;(deftest delete-tracks-deletes-a-song-from-the-queue-if-requesters-match
;  (user/sign-up! (factory/user {:login "user"}))
;  (playlist/add-song! "user/artist/album/track.mp3" {:login "user"})
;  (let [uuid (:id (first (playlist/queued-songs)))
;        request {:params {:id uuid} :headers {"accept" "application/json"} :session {:current-user "user"}}
;        response (playlist-controller/delete request)]
;    (is (= [] (playlist/queued-songs)))))
;
;(deftest delete-tracks-doesnt-delete-a-song-from-the-queue-if-user-isnt-requester
;  (user/sign-up! (factory/user {:login "user"}))
;  (playlist/add-song! "user/artist/album/track.mp3" {:login "user2"})
;  (let [uuid (:id (first (playlist/queued-songs)))
;        request {:params {:id uuid} :headers {"accept" "application/json"} :session {:current-user "user"}}
;        response (playlist-controller/delete request)]
;    (is (= 1 (count (playlist/queued-songs))))))
;
;(deftest lists-tracks-returns-the-current-playlist-as-a-json-response
;  (playlist/add-song! "user/artist/album/track.mp3")
;  (playlist/add-song! "user/artist/album/track2.mp3")
;
;  (let [request {:headers {"accept" "application/json"}}
;        uuid (:id (first (playlist/queued-songs)))
;        response (playlist-controller/index request)
;        response-json (json/parse-string (:body response))]
;    (is (= 2 (count response-json)))
;    (is (= { "playCount" 0
;               "skipCount" 0
;               "owner"     "user"
;               "requester" nil
;               "artwork"   {"large" "/img/no_art_lrg.png"  "extra-large" "/img/no_art_lrg.png"}
;               "duration"  16
;               "title"     "jukebox2"
;               "progress"  0
;               "playing"   false
;               "isRequester"   false
;               "id"   uuid
;               "album"     "Hammer's Album"
;               "artist"    "Hammer"         } (first response-json)))))
;
;(deftest show-current-track-returns-the-current-track-as-a-json-response
;  (playlist/add-song! "user/artist/album/track.mp3")
;  (playlist/next-track nil)
;
;  (let [request {:headers {"accept" "application/json"}}
;        uuid (:id (playlist/current-song))
;        response (playlist-controller/current-track request)
;        response-json (json/parse-string (:body response))]
;    (is (= {"playCount"   1
;            "skipCount"   0
;            "owner"       "user"
;            "requester"   nil
;            "duration"    16
;            "title"       "jukebox2"
;            "artwork"     {"large" "/img/no_art_lrg.png"  "extra-large" "/img/no_art_lrg.png"}
;            "progress"    0
;            "playing"     false
;            "isRequester" false
;            "id"          uuid
;            "album"       "Hammer's Album"
;            "artist"      "Hammer"}
;           response-json))))
