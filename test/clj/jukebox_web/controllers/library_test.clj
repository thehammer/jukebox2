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
