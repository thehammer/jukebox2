(ns jukebox-player.core-test
  (:use [clojure.test]
        [jukebox-player.core]))

(deftest hammertimes-work
  (testing "Leaves player paused after hammertime with a true pause argument"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [&_] nil)]
      (hammertime! nil nil nil "true")
      (is (= :pause @player-state))))

  (testing "Continues playing after pause if a hammertime's pause argument is false"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [&_] nil)]
      (hammertime! nil nil nil "false")
      (is (= :play @player-state))))

  (testing "Continues playing after pause if a hammertime's pause argument is nil"
    (binding [play-snippet (fn [arg1 arg2 arg3] nil)
              player-state (atom :play)
              load-track (fn [arg] nil)]
      (hammertime! nil nil nil nil)
      (is (= :play @player-state)))))
