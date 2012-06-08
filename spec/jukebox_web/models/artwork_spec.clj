(ns jukebox-web.models.artwork-spec
  (:require [jukebox-web.models.artwork :as artwork])
  (:use [speclj.core]
        [clojure.contrib.http.agent :only (success?)]
        [clojure.contrib.mock]))

(describe "artwork"
  (describe "album-cover"

    (it "returns default artwork if album isnt found"
      (let [album (artwork/album-cover "user" "test")]
        (should (not (nil? album)))
        (should= {:large "/img/no_art_lrg.png"
                  :extra-large "/img/no_art_lrg.png"}
                 album))))

    (it "returns default artwork if the HTTP request fails"
        (expect [success? (calls (fn [http-agent] (throw (Exception. "failed"))))]
          (let [album (artwork/album-cover "user" "test")]
            (should (not (nil? album)))
            (should= {:large "/img/no_art_lrg.png"
                      :extra-large "/img/no_art_lrg.png"}
                     album))))

    (it "finds artwork for an album"
      (let [album (artwork/album-cover "21" "Adele")]
        (should (not (nil? album)))
        (should= {:large "http://userserve-ak.last.fm/serve/174s/55125087.png"
                  :extra-large "http://userserve-ak.last.fm/serve/_/55125087/21++600x600+HQ+PNG.png"}
                 album))))
