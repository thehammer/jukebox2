(ns jukebox-web.controllers.library-spec
  (:require [jukebox-web.controllers.library :as library-controller]
            [clojure.contrib.string :as string])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "library"
  (with-database-connection)

  (describe "browse"
    (it "renders successfully"
      (let [response (library-controller/browse-root nil)]
        (should (string/substring? "Files in Music Library" response))))))
