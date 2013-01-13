(ns jukebox-web.models.artwork
  (:require [clj-http.client :as client]
            [ring.util.codec :as codec])
  (:use [clojure.data.json :only (read-str)]
        [clojure.java.io :only (reader)]))

(def default-image-path "/img/no_art_lrg.png")

(def default-images {:large default-image-path :extra-large default-image-path})

(def base-url "http://ws.audioscrobbler.com/2.0?method=album.getInfo&api_key=809bf298f1f11c57fbb680b1befdf476&format=json&autocorrect=1")

(defn url [album artist]
  (format "%s&album=%s&artist=%s" base-url (codec/url-encode album) (codec/url-encode artist)))

(defn image-for-size [images size]
  (get (first (filter #(and (= (:size %) size) (not (empty? (:#text %)))) images)) "#text"))

(defn transform [http-response]
  (let [json-response (read-str (:body http-response))
        images (get-in json-response ["album" "image"])]
    (if (empty? images)
      default-images
      {:large (image-for-size images "large")
       :extra-large (image-for-size images "mega")})))

(defn album-cover [album artist]
  (try
    (let [response (client/get (url album artist))]
      (if (= 200 (:status response))
        (transform response)
        default-images))
    (catch java.lang.Exception e (do
                                   (prn e)
                                   default-images))))

