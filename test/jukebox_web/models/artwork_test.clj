(ns jukebox-web.models.artwork-test
  (:require [jukebox-web.models.artwork :as artwork])
  (:use [clojure.test]))

(deftest shows-album-cover
  (testing "returns default artwork if album isnt found"
    (let [album (artwork/album-cover "user" "test")]
      (is (not (nil? album)))
      (is (= {:large "/img/no_art_lrg.png" :extra-large "/img/no_art_lrg.png"}
             album))))

  (testing "returns default artwork if the HTTP request fails"
    (let [album (artwork/album-cover "user" "test")]
      (is (not (nil? album)))
      (is (= {:large "/img/no_art_lrg.png" :extra-large "/img/no_art_lrg.png"}
             album))))

  (testing "finds artwork for an album"
    (let [album (artwork/album-cover "21" "Adele")]
      (is (not (nil? album)))
      (is (.startsWith (:large album) "http://userserve-ak.last.fm/serve"))
      (is (.startsWith (:extra-large album) "http://userserve-ak.last.fm/serve")))))
