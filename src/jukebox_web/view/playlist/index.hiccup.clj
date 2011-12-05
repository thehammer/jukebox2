(let [tags (:tags *view-context*)
      cong (:current-song *view-context*)]
  (list
    [:div#current_track
     [:div.song.media-grid
      [:div.album-cover {:data-thumbnail "large" :data-artist (:artist tags) :data-album (:album tags)}]
      [:div#track.meta-data
       [:h1.title (:title tags)]
       [:p.artist (:artist tags)]
       [:p.album (:album tags)]
       [:p.play-count "Play count: " (library/play-count song)]]
      [:div#player-controls.meta-data
       [:p.progress {:data-current (str (int (player/current-time))) :data-duration (str (:duration tags))}
        [:span.remaining]]
       [:p.controls
        (if (player/paused?) [:a.btn.play {:href "/player/play" :data-remote "true"} "Play"])
        (if (player/playing?) [:a.btn.pause {:href "/player/pause" :data-remote "true"} "Pause"])
        (if (player/playing?) (when-not (nil? (-> request :session :current-user)) [:a.btn.skip {:href "/player/skip" :data-remote "true"} "Skip"]))]]]
     [:h3 "Playing Music From"]
     (map (partial build-avatar current-song) (user/find-enabled))
     [:div.row
      [:h3 "Playlist"]
      [:ol#playlist.span12.clearfix
       (if-not (empty? queued-songs)
         (map #(vector :li (playlist %)) queued-songs)
         [:li.random "Choosing random tracks"])]]]))