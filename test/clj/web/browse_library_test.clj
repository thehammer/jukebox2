(ns web.browse-library-test
  (:import [java.io ByteArrayInputStream])
  (:require [jukebox-web.core :as jukebox]
            [net.cgrand.enlive-html :as html]
            [ring.util.codec :as encode])
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
    (is ((set (map html/text (find-tags resp [:ul.entries :li :a]))) "Hammer"))
    (is ((set (map (fn [tag] (-> tag :attrs :href)) (find-tags resp [:ul.entries :li :a]))) "/library/artists/Hammer"))
    ))

(deftest can-browse-albums
  (let [resp (-> (session jukebox/test-app)
                 (request "/library/artists/Hammer"))]
    (is (= 200 (-> resp :response :status)))
    (is ((set (map html/text (find-tags resp [:ul.entries :li :a]))) "Hammer's Album"))
    (is ((set (map (fn [tag] (-> tag :attrs :href)) (find-tags resp [:ul.entries :li :a]))) "/library/artists/Hammer/albums/Hammer%27s%20Album"))
    ))

(deftest can-browse-tracks
  (let [resp (-> (session jukebox/test-app)
                 (request "/library/artists/Hammer/albums/Hammer%27s%20Album"))]
    (is (= 200 (-> resp :response :status)))
    (is ((set (map html/text (find-tags resp [:ul.entries :li]))) "jukebox2"))
    ))

(deftest logged-in-user-can-add-track
  (let [resp (-> (session jukebox/test-app)
                 (request "/users/authenticate"
                          :request-method :post
                          :params { :login "user"
                                    :password "secret" })
                 (request "/library/artists/Hammer/albums/Hammer%27s%20Album"))]
    (is (= 200 (-> resp :response :status)))
    (is ((set (map html/text (find-tags resp [:ul.entries :li :a]))) "jukebox2"))
    (is ((set (map (fn [tag] (-> tag :attrs :href)) (find-tags resp [:ul.entries :li :a]))) "/playlist/add/jukebox2"))))
