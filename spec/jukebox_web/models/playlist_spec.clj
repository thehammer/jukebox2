(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "playlist"
  (with-test-music-library)
  (with-database-connection)
  (with-smaller-weight-threshold)

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

    (describe "weighted-users"
      (it "gives users with song counts past threshold a higher chance of being chosen"
        (user/toggle-enabled! "user2")
        (should= ["user2" "user" "user" "user" "user" "user"] (playlist/weighted-users))))

    (describe "add-song!"
      (with-test-music-library)

      (it "adds the given song to the queued songs"
          (should (empty? (playlist/queued-songs)))
          (playlist/add-song! "user/artist/album/track.mp3")
          (should= 1 (count (playlist/queued-songs)))
          (should= (library/file-on-disk "user/artist/album/track.mp3") (:song (first (playlist/queued-songs)))))

      (it "adds the song with a unique id"
          (should (empty? (playlist/queued-songs)))
          (playlist/add-song! "user/artist/album/track.mp3")
          (should= 1 (count (playlist/queued-songs)))
          (should (not (nil? (:id (first (playlist/queued-songs)))))))

      (it "adds the song to the end of the queue"
          (playlist/add-song! "user/artist/album/track.mp3")
          (let [first-value (first (playlist/queued-songs))]
            (playlist/add-song! "user/artist/album/track2.mp3")
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

    (describe "queued-song"
      (it "grabs a queued song based on id"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track.mp3")
        (let [uuid (:id (first (playlist/queued-songs)))
              song (playlist/queued-song uuid)]
          (should= (:id (first (playlist/queued-songs))) (:id song))))

      (it "returns nil if song doesn't exist"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (should (nil? (playlist/queued-song "0")))))

    (describe "delete-song!"
      (it "deletes a song when given a playlist id"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track.mp3")
        (let [uuid (:id (first (playlist/queued-songs)))]
          (playlist/delete-song! uuid)
          (should= 1 (count (playlist/queued-songs)))
          (should (not (= (:id (first (playlist/queued-songs))) uuid)))))

      (it "adds a song to the end of the queue after deleting a song"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track.mp3")
        (let [uuid (:id (first (playlist/queued-songs)))
              last-uuid (:id (last (playlist/queued-songs)))]
          (playlist/delete-song! uuid)
          (playlist/add-song! "user/artist/album/track.mp3")
          (should= (:id (first (playlist/queued-songs))) last-uuid)))

      (it "leaves the queue alone if id isn't found"
        (playlist/reset-state!)
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track.mp3")
        (let [uuid (:id (first (playlist/queued-songs)))]
          (playlist/delete-song! "0")
          (should= 2 (count (playlist/queued-songs)))
          (should= (:id (first (playlist/queued-songs))) uuid))))

    (describe "next-track"
      (with-test-music-library)

      (it "returns the next track in the queue, retaining order"
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track2.mp3")
        (playlist/add-song! "user/artist/album/track2.mp3")
        (let [next-track (playlist/next-track "")]
          (playlist/add-song! "user/artist/album/track2.mp3")
          (should= (library/file-on-disk "user/artist/album/track.mp3") next-track)
          (should= (library/file-on-disk "user/artist/album/track2.mp3") (:song (first (playlist/queued-songs))))
          (should= (map library/file-on-disk ["user/artist/album/track2.mp3" "user/artist/album/track2.mp3"]) (map :song (rest (playlist/queued-songs))))))

      (it "increments play-count when moving to next track"
        (playlist/add-song! "user/artist/album/track.mp3")
        (playlist/add-song! "user/artist/album/track2.mp3")
        (let [next-track (playlist/next-track "")]
          (should= 1 (library/play-count (library/file-on-disk "user/artist/album/track.mp3")))
          (should= 0 (library/play-count (library/file-on-disk "user/artist/album/track2.mp3")))
          (playlist/next-track "")
          (should= 1 (library/play-count (library/file-on-disk "user/artist/album/track2.mp3"))))))

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
