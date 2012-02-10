(ns jukebox-player.core-spec
  (:use [speclj.core]
        [jukebox-player.core]))

(describe "hammertime!"

  (it "Leaves player paused after hammertime with a true pause argument"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [&_] nil)]
      (hammertime! nil nil nil "true")
      (should= :pause @player-state)))

  (it "Continues playing after pause if a hammertime's pause argument is false"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [&_] nil)]
      (hammertime! nil nil nil "false")
      (should= :play @player-state)))

  (it "Continues playing after pause if a hammertime's pause argument is nil"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [arg] nil)]
      (hammertime! nil nil nil nil)
      (should= :play @player-state)))

  )
