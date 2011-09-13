(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist])
  (:use [speclj.core]))

(describe "playlist"
  (before
    (playlist/reset-state!))

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
