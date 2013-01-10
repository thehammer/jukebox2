(ns jukebox-web.models.playlist-test
  (:require [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each
              with-test-music-library
              with-database-connection
              with-smaller-weight-threshold
              (fn [f] (playlist/reset-state!) (f)))

(deftest adds-a-random-song
  (playlist/reset-state!)
  (is (empty? (playlist/queued-songs)))
  (playlist/add-random-song!)
  (is (= 1 (count (playlist/queued-songs)))))

(deftest gives-users-with-lots-of-songs-higher-chance-of-being-chosen
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (playlist/reset-state!)

  (is (= ["user2" "user" "user" "user" "user" "user"]
         (playlist/weighted-users))))

(deftest adding-songs
  (is (empty? (playlist/queued-songs)))
  (playlist/add-song! "user/artist/album/track.mp3")
  (is (= 1 (count (playlist/queued-songs))))

  (testing "adds the given song to the queued songs"
    (is (= (library/file-on-disk "user/artist/album/track.mp3")
           (:song (first (playlist/queued-songs))))))
  (testing "adds the song with a unique id"
    (is (not (nil? (:id (first (playlist/queued-songs))))))))

(deftest adds-songs-to-end-of-queue
  (playlist/add-song! "user/artist/album/track.mp3")
  (let [first-value (first (playlist/queued-songs))]
    (playlist/add-song! "user/artist/album/track2.mp3")
    (is (= first-value (first (playlist/queued-songs))))))

(deftest add-album-adds-all-songs-in-album-to-queue
  (playlist/add-album! "user/artist/album")
  (is (= 2 (count (playlist/queued-songs))))
  (is (= #{(library/file-on-disk "user/artist/album/track.mp3")
           (library/file-on-disk "user/artist/album/track2.mp3")}
         (set (map :song (playlist/queued-songs))))))

(deftest can-add-random-song
  (is (empty? (playlist/queued-songs)))
  (playlist/add-random-song!)
  (is (= 1 (count (playlist/queued-songs)))))

(deftest adds-random-songs-to-end-of-queue
  (playlist/add-random-song!)
  (let [first-value (first (playlist/queued-songs))]
    (playlist/add-random-song!)
    (is (= first-value (first (playlist/queued-songs))))))

(deftest only-adds-random-songs-from-enabled-users
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (user/toggle-enabled! "user")
  (playlist/add-random-song!)
  (let [next-track (first (playlist/queued-songs))]
    (is (= "user2" (library/owner (:song next-track))))))

(deftest does-not-add-recently-played-random-songs
  (loop [count 0]
    (playlist/reset-state!)
    (playlist/add-song! "user/artist/album/track.mp3")
    (playlist/add-random-song!)
    (is (not (= (first (playlist/queued-songs))
                (last (playlist/queued-songs)))))
    (if (< count 10) (recur (inc count)))))

(deftest has-a-limit-for-history-of-recently-played-songs
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track2.mp3")
  (playlist/add-song! "user/artist/album2/track.mp3")
  (playlist/add-song! "jukebox2.mp3")
  (playlist/add-song! "jukebox2.ogg")
  (playlist/add-random-song!)
  (playlist/add-random-song!)
  (is (= 7 (count (playlist/queued-songs)))))

(deftest queued-song-found-by-id
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track.mp3")
  (let [uuid (:id (first (playlist/queued-songs)))
        song (playlist/queued-song uuid)]
    (is (= (:id (first (playlist/queued-songs))) (:id song)))))

(deftest queued-songs-returns-nil-if-not-queued
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (is (nil? (playlist/queued-song "0"))))

(deftest can-delete-a-song-by-id
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track.mp3")
  (let [uuid (:id (first (playlist/queued-songs)))]
    (playlist/delete-song! uuid)
    (is (= 1 (count (playlist/queued-songs))))
    (is (not (= (:id (first (playlist/queued-songs))) uuid)))))

(deftest deleting-adds-another-song-to-queue
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track.mp3")
  (let [uuid (:id (first (playlist/queued-songs)))
        last-uuid (:id (last (playlist/queued-songs)))]
    (playlist/delete-song! uuid)
    (playlist/add-song! "user/artist/album/track.mp3")
    (is (= (:id (first (playlist/queued-songs))) last-uuid))))

(deftest delete-does-nothing-if-id-is-not-found
  (playlist/reset-state!)
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track.mp3")
  (let [uuid (:id (first (playlist/queued-songs)))]
    (playlist/delete-song! "0")
    (is (= 2 (count (playlist/queued-songs))))
    (is (= (:id (first (playlist/queued-songs))) uuid))))

(deftest next-track-returns-the-next-track-on-the-queue
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track2.mp3")
  (playlist/add-song! "user/artist/album/track2.mp3")
  (let [next-track (playlist/next-track "")]
    (playlist/add-song! "user/artist/album/track2.mp3")
    (is (= (library/file-on-disk "user/artist/album/track.mp3") next-track))
    (is (= (library/file-on-disk "user/artist/album/track2.mp3")
           (:song (first (playlist/queued-songs)))))
    (is (= (map library/file-on-disk ["user/artist/album/track2.mp3" "user/artist/album/track2.mp3"])
           (map :song (rest (playlist/queued-songs)))))))

(deftest next-track-increments-play-count
  (playlist/add-song! "user/artist/album/track.mp3")
  (playlist/add-song! "user/artist/album/track2.mp3")
  (let [next-track (playlist/next-track "")]
    (is (= 1 (library/play-count (library/file-on-disk "user/artist/album/track.mp3"))))
    (is (= 0 (library/play-count (library/file-on-disk "user/artist/album/track2.mp3"))))
    (playlist/next-track "")
    (is (= 1 (library/play-count (library/file-on-disk "user/artist/album/track2.mp3"))))))

(deftest playlist-seq-returns-random-track-if-no-tracks-in-queue
  (is (empty? (playlist/queued-songs)))
  (is (not (nil? (first (playlist/playlist-seq))))))

(deftest playlist-seq-returns-first-queued-song
  (playlist/add-random-song!)
  (let [expected (:song (first (playlist/queued-songs)))]
    (is (= expected (first (playlist/playlist-seq))))))

(deftest playlist-seq-returns-random-tracks-indefinitely
  (is (not (nil?  (first (drop 10 (playlist/playlist-seq)))))))
