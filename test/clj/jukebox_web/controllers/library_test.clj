(ns jukebox-web.controllers.library-test
  (:require [cheshire.core :as json]
            [jukebox-web.controllers.library :as library-controller])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest return-artists
  (let [response (library-controller/artists {})]
    (testing "renders json successfully"
      (is (= 200 (:status response)))
      (is (= "application/json" (get-in response [:headers "Content-Type"]))))
    (testing "returns a list of all artists"
      (is (= ["artist" "artist2"]
             (map #(get % "artist") (json/parse-string (:body response))))))))


(deftest return-albums-for-artist
  (let [response (library-controller/albums-for-artist {:params {:artist "artist"}})]
    (testing "renders json successfully"
      (is (= 200 (:status response)))
      (is (= "application/json" (get-in response [:headers "Content-Type"]))))
    (testing "returns a list of the artist's albums"
      (is (= ["album" "album2"]
             (map #(get % "album") (json/parse-string (:body response))))))))

(deftest return-tracks-for-artists-album
  (let [response (library-controller/tracks-for-album {:params {:artist "artist" :album "album"}})]
    (testing "renders json successfully"
      (is (= 200 (:status response)))
      (is (= "application/json" (get-in response [:headers "Content-Type"]))))
    (testing "returns a list of the artist's albums"
      (is (= ["track" "track2"]
             (map #(get % "title") (json/parse-string (:body response))))))))
