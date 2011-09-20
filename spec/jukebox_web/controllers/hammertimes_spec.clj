(ns jukebox-web.controllers.player-spec
  (:require [jukebox-web.controllers.hammertimes :as hammertimes-controller])
  (:require [jukebox-web.models.hammertime :as hammertime])
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "create"
  (with-database-connection)

  (it "saves valid hammertimes"
    (let [request {:params (factory/hammertime {:name "test"})}
          response (hammertimes-controller/create request)]
      (should-not (nil? (hammertime/find-by-name "test")))))

  (it "renders errors when not valid"
    (let [request {:params (factory/hammertime {:name nil})}
          response (hammertimes-controller/create request)]
      (should= nil (:headers response))
      (should (nil? (hammertime/find-by-name "test")))))

  (it "redirects to playlist after saving"
    (let [request {:params (factory/hammertime {})}
          response (hammertimes-controller/create request)]
      (should= 302 (:status response))
      (should= {"Location" "/playlist"} (:headers response)))))

