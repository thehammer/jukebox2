(ns jukebox-web.controller.playlist-controller
  (:use
    [compojure.core]
    [ring.util.response :only (redirect)]
    [joodo.middleware.request :only (*request*)]
    [joodo.views :only (render-template)]
    [jukebox-web.util.encoding :only [sha256]]
    [jukebox-player.tags :only (extract-tags)])
  (:require
    [jukebox-player.core :as player]
    [jukebox-web.util.json :as json]
    [jukebox-web.views.playlist :as view]
    [jukebox-web.models.playlist :as playlist]
    [jukebox-web.models.library :as library]
    [jukebox-web.models.user :as user]))

(defn- build-playlist []
  (let [songs (playlist/queued-songs)]
    (if-not (empty? songs)
      (map #(extract-tags %) songs)
      {})))

(defn index [request]
  (if (json/request? (get (:headers request) "accept"))
    (json/response (build-playlist))
    (let [current-song (playlist/current-song)
          tags (extract-tags current-song)]
      (render-template "playlist/index"
        :current-song current-song
        :queued-songs (playlist/queued-songs)
        :title (str (:title tags) " - " (:artist tags))))))

(defn current-track [request]
  (let [song (playlist/current-song)
        html (view/current-track request song (playlist/queued-songs))
        etag (sha256 html)
        loggedin (not (nil? (-> request :session :current-user)))
        progress (int (player/current-time))]
    (if (json/request? ((:headers request) "accept"))
      (json/response (merge (extract-tags song) {:owner (library/owner song) :progress progress :playing (player/playing?) :canSkip loggedin :playCount (library/play-count song)}))
      {:status 200 :headers {"E-Tag" etag "X-Progress" (str progress)} :body html})))

(defn- playlist-response []
  (if (json/request? (get (:headers *request*) "accept"))
    (json/response (build-playlist))
    (redirect "/playlist")))

(defn add-one []
  (playlist/add-random-song!)
  (playlist-response))

(defn add
  ([] (add (-> *request* :params :song)))
  ([song]
    (playlist/add-song! song)
    (playlist-response)))

(defroutes playlist-controller
  (GET "/playlist" [] index)
  (GET "/playlist/current-track" [] current-track)
  (GET "/playlist/add-one" [] (add-one))
  (POST "/playlist/add" [] (add))
  (GET ["/playlist/add/:song" :song #".*"] {{song :song} :params} (add song)))
