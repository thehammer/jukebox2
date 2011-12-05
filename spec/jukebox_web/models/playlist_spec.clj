(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "playlist"
  (with-test-music-library)
  (with-database-connection)

  (context "without users"
    (before
      (playlist/reset-state!))

    (describe "add-random-song!"
      (it "adds a random song to the queued songs"
          (should (empty? (playlist/queued-songs)))
          (playlist/add-random-song!)
          (should= 1 (count (playlist/queued-songs))))))

  (context "with users"
    (before
      (user/sign-up! (factory/user {:login "user"}))
      (user/sign-up! (factory/user {:login "user2"}))
      (user/toggle-enabled! "user2")
      (playlist/reset-state!))

    (describe "add-song!"
      (it "adds the given song to the queued songs"
          (should (empty? (playlist/queued-songs)))
          (playlist/add-song! "user/artist/track.mp3")
          (should= 1 (count (playlist/queued-songs)))
          (should= (library/file-on-disk "user/artist/track.mp3") (:song (first (playlist/queued-songs)))))

      (it "adds the song to the end of the queue"
          (playlist/add-song! "user/artist/first_track.mp3")
          (let [first-value (first (playlist/queued-songs))]
            (playlist/add-song! "user/artist/second_track.mp3")
            (should= first-value (first (playlist/queued-songs))))))

    (describe "add-random-song!"
      (it "adds a random song to the queued songs"
          (should (empty? (playlist/queued-songs)))
          (playlist/add-random-song!)
          (should= 1 (count (playlist/queued-songs))))

      (it "adds the song to the end of the queue"
          (playlist/add-random-song!)
          (let [first-value (first (playlist/queued-songs))]
            (playlist/add-random-song!)
            (should= first-value (first (playlist/queued-songs)))))

      (it "only selects songs from enabled users"
        (user/toggle-enabled! "user")
        (user/toggle-enabled! "user2")
        (playlist/add-random-song!)
        (let [next-track (first (playlist/queued-songs))]
          (should= "user2" (library/owner (:song next-track)))))

      (it "adds a random song that has not been recently played"
        (loop [count 0]
          (playlist/reset-state!)
          (playlist/add-song! "user/artist/album/track.mp3")
          (playlist/add-random-song!)
          (should-not= (first (playlist/queued-songs)) (last (playlist/queued-songs)))
          (if (< count 10) (recur (inc count)))))

      (it "will only track a portion of the library songs as recently played"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track2.mp3")
        (playlist/add-song! "user/artist/album2/track.mp3")
        (playlist/add-song! "jukebox2.mp3")
        (playlist/add-song! "jukebox2.ogg")
        (playlist/add-random-song!)
        (playlist/add-random-song!)
        (should= 7 (count (playlist/queued-songs)))))

    (describe "next-track"
      (it "returns the next track in the queue, retaining order"
        (playlist/add-song! "track-a")
        (playlist/add-song! "track-b")
        (playlist/add-song! "track-c")
        (let [next-track (playlist/next-track "")]
          (playlist/add-song! "track-d")
          (should= (library/file-on-disk "track-a") next-track)
          (should= (library/file-on-disk "track-b") (:song (first (playlist/queued-songs))))
          (should= (map library/file-on-disk ["track-c" "track-d"]) (map :song (rest (playlist/queued-songs))))))

      (it "increments play-count when moving to next track"
        (playlist/add-song! "track-a")
        (playlist/add-song! "track-b")
        (let [next-track (playlist/next-track "")]
          (should= 1 (library/play-count (library/file-on-disk "track-a")))
          (should= 0 (library/play-count (library/file-on-disk "track-b")))
          (playlist/next-track "")
          (should= 1 (library/play-count (library/file-on-disk "track-b"))))))

    (describe "playlist-seq"
      (it "returns a random track if there are no tracks queued up"
        (should (empty? (playlist/queued-songs)))
        (should-not-be-nil (first (playlist/playlist-seq))))

      (it "returns the first queued song, ready for playing"
        (playlist/add-random-song!)
        (let [expected (:song (first (playlist/queued-songs)))]
          (should= expected (first (playlist/playlist-seq)))))

      (it "will return random tracks indefinitely"
        (should-not-be-nil (first (drop 10 (playlist/playlist-seq))))))))
