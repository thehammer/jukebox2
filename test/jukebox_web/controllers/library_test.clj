(ns jukebox-web.controllers.library-test
  (:require [jukebox-web.controllers.library :as library-controller])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection)

(deftest browse-library
  (testing "renders successfully"
    (let [response (library-controller/browse-root nil)]
      (is (.contains response "Files in Music Library")))))
