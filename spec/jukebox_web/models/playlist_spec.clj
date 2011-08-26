(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.playlist :as playlist])
  (:use [speclj.core]))

(describe "add-random-song!"
  (it "adds a random song to the queued songs"
      (playlist/reset-state!)
      (should (empty? (playlist/queued-songs)))
      (playlist/add-random-song!)
      (should= 1 (count (playlist/queued-songs)))))

(describe "set-current-song!"
  (it "sets the current song to a random song if the queue is empty"
      (playlist/reset-state!)
      (should (empty? (playlist/queued-songs)))
      (playlist/set-current-song!)
      (should-not= nil (playlist/current-song)))

  (it "sets the current song to to the first song in the queue if the queue is not empty"
      (playlist/reset-state!)
      (playlist/add-random-song!)
      (let [next-in-queue (playlist/next-song)]
        (playlist/set-current-song!)
        (should= next-in-queue (playlist/current-song)))))

(run-specs)
