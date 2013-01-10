(ns jukebox-web.util.json-test
  (:require [jukebox-web.util.json :as json-file]
            [clojure.java.io :as io])
  (:use [clojure.test]
        [clojure.contrib.seq :only [includes?]]))

(deftest builds-json-responses
  (testing "supports utf-8 characters"
     (is (= {:status 200,
             :headers {"Content-Type" "application/json; charset=utf-8"},
             :body "{\"foo\":\"h\u00e8llo\"}"}
            (json-file/response {:foo "h\u00e8llo"})))))
