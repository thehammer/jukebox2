(ns jukebox-web.models.artwork-spec
  (:require [jukebox-web.models.artwork :as artwork])
  (:use [speclj.core]))

(describe "artwork"
  (describe "album-cover"

    (it "returns default artwork if album isnt found"
      (let [album (artwork/album-cover "user" "test")]
        (should (not (nil? album)))
        (should= album "no_art_lrg.png")))

    (it "finds artwork for an album"
      (let [album (artwork/album-cover "21" "Adele")]
        (should (not (nil? album)))
        (should= album "http://userserve-ak.last.fm/serve/174s/55125087.png")))))

