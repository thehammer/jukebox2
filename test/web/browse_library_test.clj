(ns web.browse-library-test
  (:import [java.io ByteArrayInputStream])
  (:require [jukebox-web.core :as jukebox]
            [net.cgrand.enlive-html :as html])
  (:use [clojure.test]
        [peridot.core]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(defn find-tags [response pattern]
  (-> (html/html-resource (-> response :response :body (.getBytes "UTF-8") ByteArrayInputStream.))
                          (html/select pattern)))

(deftest can-browse-artists
  (let [resp (-> (session jukebox/test-app)
                 (request "/library/artists"))]
    (is (= 200 (-> resp :response :status)))
    (is ((set (map html/text (find-tags resp [:ul.entries :li]))) "Hammer"))))
