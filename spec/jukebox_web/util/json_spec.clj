(ns jukebox-web.util.json-spec
  (:require [jukebox-web.util.json :as json-file]
            [clojure.java.io :as io])
  (:use [speclj.core]
        [clojure.contrib.seq :only [includes?]]
        [jukebox-web.spec-helper]))

(describe "response"
  (it "supports utf-8 characters"
     (should= {:status 200,
               :headers {"Content-Type" "application/json; charset=utf-8"},
               :body "{\"foo\":\"h\u00e8llo\"}"}
              (json-file/response {:foo "h\u00e8llo"}))))

