(ns jukebox-web.controllers.library-spec
  (:require [jukebox-web.controllers.library :as library-controller]
            [clojure.contrib.string :as string])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "library"
  (with-database-connection)

  (describe "most-played"
    (it "renders successfully"
      (let [response (library-controller/most-played nil)]
        (should (string/substring? "Most Played Tracks" response))))))
