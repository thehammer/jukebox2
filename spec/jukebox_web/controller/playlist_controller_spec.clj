(ns jukebox-web.controller.playlist-controller-spec
  (:require
    [jukebox-web.models.playlist :as playlist]
    [jukebox-web.models.library :as library])
  (:use
    [speclj.core]
    [joodo.spec-helpers.controller]
    [jukebox-web.controller.playlist-controller]
    [jukebox-web.spec-helper]
    [jukebox-player.tags :only (extract-tags)]))

(describe "Playlist controller"

  (with-mock-rendering)
  (with-routes playlist-controller)
  (with bauble (atom nil))
  (before (playlist/reset-state!))

  (it "adds the given file to the end of the queued-songs"
    (let [song "user/artist/album/track.mp3"
          response (do-post "/playlist/add" :params {:song song})]
      (should-redirect-to response "/playlist")
      (should= (library/file-on-disk song) (first (playlist/queued-songs)))))

  (it "adds a song using GET"
    (let [song "user/artist/album/track.mp3"
          response (do-get (str "/playlist/add/" song))]
      (should-redirect-to response "/playlist")
      (should= (library/file-on-disk song) (first (playlist/queued-songs)))))

  (it "adds a random track"
    (binding [playlist/add-random-song! #(reset! @bauble :random-song-added)]
      (let [response (do-get "/playlist/add-one")]
        (should-redirect-to response "/playlist")
        (should= :random-song-added @@bauble))))

  (it "renders the playlist"
    (binding [playlist/current-song (fn [] :current-song)
              playlist/queued-songs (fn [] :queued-songs)
              extract-tags (fn [song] {:title "Title" :artist "Artist"})]
      (let [response (do-get "/playlist")]
        (should= 200 (:status response))
        (should= "playlist/index" @rendered-template)
        (should= :current-song (:current-song @rendered-context))
        (should= :queued-songs (:queued-songs @rendered-context))
        (should= "Title - Artist" (:title @rendered-context)))))

  )

