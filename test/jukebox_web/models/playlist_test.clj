(ns jukebox-web.models.playlist-test
  (:require [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each
              with-database-connection
              with-test-music-library
              with-smaller-weight-threshold
              (fn [f] (playlist/reset-state!) (f)))

;(deftest adds-a-random-song
;  (playlist/reset-state!)
;  (is (empty? (playlist/queued-songs)))
;  (playlist/add-random-song!)
;  (is (= 1 (count (playlist/queued-songs)))))

(deftest gives-users-with-lots-of-songs-higher-chance-of-being-chosen
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (playlist/reset-state!)

  (is (= ["user" "user" "user" "user" "user" "user2"]
         (map :login (playlist/weighted-users)))))

(deftest adding-songs
  (let [track-to-add (library/random-track)]
    (is (empty? (playlist/queued-songs)))
    (playlist/add-song! track-to-add)
    (is (= 1 (count (playlist/queued-songs))))

    (testing "adds the given song to the queued songs"
      (is (= (:id track-to-add)
             (:id (first (playlist/queued-songs))))))

    (testing "adds the song with a unique id"
      (is (not (nil? (:playlist-id (first (playlist/queued-songs)))))))))

(deftest adds-songs-to-end-of-queue
  (playlist/add-song! (library/find-by-id 1))
  (let [first-value (first (playlist/queued-songs))]
    (playlist/add-song! (library/find-by-id 2))
    (is (= first-value (first (playlist/queued-songs))))))

(deftest add-album-adds-all-songs-in-album-to-queue
  (playlist/add-album! "artist" "album")
  (is (= 2 (count (playlist/queued-songs))))
  (is (= #{"test/fixtures/music/user/artist/album/track.mp3"
           "test/fixtures/music/user/artist/album/track2.mp3"}
         (set (map :tempfile_location (playlist/queued-songs))))))

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
  (user/toggle-enabled! "user")
  (playlist/add-random-song!)
  (is (= "user2" (:login (library/owner-md (first (playlist/queued-songs)))))))

(deftest does-not-add-recently-played-random-songs
  (loop [count 0]
    (playlist/reset-state!)
    (playlist/add-song! (library/find-by-id 1))
    (playlist/add-random-song!)
    (is (not (= (first (playlist/queued-songs))
                (last (playlist/queued-songs)))))
    (if (< count 10) (recur (inc count)))))

(deftest has-a-limit-for-history-of-recently-played-songs
  (playlist/reset-state!)
  (doseq [track (library/find-all)]
    (playlist/add-song! track))
  (playlist/add-random-song!)
  (playlist/add-random-song!)
  (is (= 6 (count (playlist/queued-songs)))))

(deftest queued-song-found-by-playlist-id
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (let [uuid (:playlist-id (first (playlist/queued-songs)))
        song (playlist/queued-song uuid)]
    (is (= (:id (first (playlist/queued-songs))) (:id song)))))

(deftest queued-songs-returns-nil-if-not-queued
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (is (nil? (playlist/queued-song "0"))))

(deftest can-delete-a-song-by-id
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (let [uuid (:playlist-id (first (playlist/queued-songs)))]
    (playlist/delete-song! uuid)
    (is (= 1 (count (playlist/queued-songs))))
    (is (not (= (:playlist-id (first (playlist/queued-songs)))
                uuid)))))

(deftest deleting-adds-another-song-to-queue
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (let [uuid (:playlist-id (first (playlist/queued-songs)))
        last-uuid (:playlist-id (last (playlist/queued-songs)))]
    (playlist/delete-song! uuid)
    (playlist/add-song! (library/find-by-id 3))
    (is (= (:playlist-id (first (playlist/queued-songs)))
           last-uuid))))

(deftest delete-does-nothing-if-id-is-not-found
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (let [uuid (:playlist-id (first (playlist/queued-songs)))]
    (playlist/delete-song! "0")
    (is (= 2 (count (playlist/queued-songs))))
    (is (= (:playlist-id (first (playlist/queued-songs)))
           uuid))))

(deftest next-track-returns-the-next-track-on-the-queue
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (playlist/add-song! (library/find-by-id 3))
  (let [next-track (playlist/next-track "")]
    (playlist/add-song! (library/find-by-id 4))
    (is (= 1 (:id next-track)))
    (is (= 2 (:id (first (playlist/queued-songs)))))
    (is (= [3 4] (map :id (rest (playlist/queued-songs)))))))

(deftest next-track-increments-play-count
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (let [next-track (playlist/next-track "")]
    (is (= 1 (library/play-count 1)))
    (is (= 0 (library/play-count 2)))
    (playlist/next-track "")
    (is (= 1 (library/play-count 2)))))

(deftest playlist-seq-returns-random-track-if-no-tracks-in-queue
  (is (empty? (playlist/queued-songs)))
  (is (not (nil? (first (playlist/playlist-seq))))))

(deftest playlist-seq-returns-first-queued-song
  (playlist/add-random-song!)
  (is (= (library/file-on-disk (first (playlist/queued-songs)))
         (first (playlist/playlist-seq)))))

(deftest playlist-seq-returns-random-tracks-indefinitely
  (is (not (nil? (first (drop 10 (playlist/playlist-seq)))))))
