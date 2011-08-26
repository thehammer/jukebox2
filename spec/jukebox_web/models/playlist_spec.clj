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
        (let [first-value (playlist/next-song)]
          (playlist/add-random-song!)
          (should= first-value (playlist/next-song)))))

  (describe "set-current-song!"
    (it "sets the current song to a random song if the queue is empty"
        (should (empty? (playlist/queued-songs)))
        (playlist/set-current-song!)
        (should-not= nil (playlist/current-song)))

    (it "sets the current song to to the first song in the queue if the queue is not empty"
        (playlist/add-random-song!)
        (let [next-in-queue (playlist/next-song)]
          (playlist/set-current-song!)
          (should= next-in-queue (playlist/current-song))))))

(run-specs)
