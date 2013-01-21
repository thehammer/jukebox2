(ns jukebox.player-test
  (:use-macros [jukebox.test-macros :only [deftest is]])
  (:require [jukebox.player :as player]
            [domina :as dom]
            [domina.css :as css]
            [goog.debug :as debug]
            [goog.testing.jsunit :as jsunit]))

(deftest shows-pause-button-when-playing
  (let [player (player/player-controls {"player" {"playing?" true}})]
    (is (empty? (dom/nodes (css/sel player ".icon-play"))))
    (is (not (empty? (dom/nodes (css/sel player ".icon-pause")))))))

(deftest toggling-pause-and-play
  (is (= {"player" {"playing?" true}}
         (player/toggle-playing {"player" {"playing?" false}})))
  (is (= {"player" {"playing?" false}}
         (player/toggle-playing {"player" {"playing?" true}}))))
