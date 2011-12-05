(doctype :html5)
[:html
 (eval (:template-body joodo.views/*view-context*))]
;  [:title (str title " - jukebox2")]
;  [:script {:src "http://use.typekit.com/ygg5mdb.js"}]
;  [:script {:src "/js/v/jquery-1.6.4.min.js"}]
;  [:script {:src "/js/v/underscore-min.js"}]
;  [:script {:src "/js/jukebox.js"}]
;  [:script {:src "/js/keyboard_shortcuts.js"}]
;  [:script {:src "/js/progress.js"}]
;  [:script {:src "/js/uploader.js"}]
;  [:script {:src "/js/notifications.js"}]
;  [:script {:src "/js/artwork.js"}]
;  [:script {:src "/js/player.js"}]
;  [:script {:src "/js/playlist.js"}]
;  [:script {:src "/js/files.js"}]
;  [:script {:src "/js/track_search.js"}]
;  [:script {:src "/js/v/bootstrap-dropdown.js"}]
;  (include-css "/css/v/bootstrap-1.3.0.min.css")
;  (include-css "/css/style.css")]
; [:body {:data-accept "mp3|m4a|mp4|mpeg"}
;  [:div.topbar
;   [:div.fill
;    [:div.container
;     [:a.brand {:href "/"} "jukebox2"]
;     (nav-links request)
;     (track-search/display-search request)
;     (if (nil? current-user)
;       (login-form)
;       (logged-in current-user))]]]
;  [:ul#notifications]
;  [:div.container
;   [:div.content
;    (str
;      (if (-> request :flash :success) (html [:div {:class "alert-message success"} (-> request :flash :success)]))
;      (html content))]]
;  (current-track-template request)
;  (player-controls-template request)
;  (playlist-template request)
;  [:script#file-notification {:type "text/example"}
;   [:li.uploading.alert-message.block-message
;    [:p "{{ file.name }} {{ file.size }}mb"]
;    [:div.progress-wrapper
;     [:div.progress-bar]]]]]
