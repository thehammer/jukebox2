(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist])
  (:use [speclj.core]))

(describe "playlist"
  (before
    (playlist/reset-state!))

  (describe "add-song!"
    (it "adds the given song to the queued songs"
        (should (empty? (playlist/queued-songs)))
        (playlist/add-song! "user/artist/track.mp3")
        (should= 1 (count (playlist/queued-songs)))
        (should= "user/artist/track.mp3" (first (playlist/queued-songs))))

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
          (should= first-value (first (playlist/queued-songs))))))

  (describe "next-track"
    (it "returns the next track in the queue, retaining order"
      (playlist/add-song! "track-a")
      (playlist/add-song! "track-b")
      (playlist/add-song! "track-c")
      (let [next-track (playlist/next-track "")]
        (playlist/add-song! "track-d")
        (should= "track-a" next-track)
        (should= "track-b" (first (playlist/queued-songs)))
        (should= ["track-c" "track-d"] (rest (playlist/queued-songs))))))

  (describe "playlist-seq"
    (it "returns a random track if there are no tracks queued up"
      (should (empty? (playlist/queued-songs)))
      (should-not (nil? (first (playlist/playlist-seq)))))

    (it "returns the first queued track"
      (playlist/add-random-song!)
      (let [expected (first (playlist/queued-songs))]
        (should= expected (first (playlist/playlist-seq)))))

    (it "will return random tracks indefinitely"
      (should-not (nil? (first (drop 10 (playlist/playlist-seq))))))))

(run-specs)
