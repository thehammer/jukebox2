(ns jukebox-web.views.layout
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.views.track-search :as track-search])
  (:use [hiccup core page form]
        [jukebox-web.views.forms]))

(defn- login-form []
  [:div.pull-right
   [:ul
    [:li.dropdown.persist {:data-dropdown "dropdown"}
     [:a.dropdown-toggle "who are you?"]
     [:ul.dropdown-menu
      [:form.login {:method :post :action "/users/authenticate"}
       [:input.input-small {:type "text" :placeholder "login" :name "login"}]
       [:input.input-small {:type "password" :placeholder "password" :name "password"}]
       [:input.btn.success {:type "submit" :value "Sign In"}]]]]]])

(defn- logged-in [current-user]
  [:div.pull-right.logged-in
    [:ul
     [:li.dropdown {:data-dropdown "dropdown"}
      [:a.dropdown-toggle (:login current-user) ]
      [:ul.dropdown-menu
       [:li
        [:button#enable-notifications.btn.success "Enable Notifications"]]
       [:li (form-to [:post "/users/sign-out"]
                     (danger-submit-button "Sign Out"))]]]
     [:img {:src (user/avatar-url current-user {:s "37"})}]]])

(defn- nav-links [request user]
  [:ul.nav
   [:li.dropdown {:data-dropdown "dropdown"}
    [:a.dropdown-toggle {:href "#"} "Add"]
    [:ul.dropdown-menu
     (when (user/canAdd? user)
       [:li [:a#random.update-playlist {:href "/playlist/add-one" :data-remote "true"} "Random"]])
     [:li [:a {:href "/library/artists"} "Browse Library"]]]]
   [:li [:a {:href "/stats"} "Stats"]]
   [:li [:a {:href "/users"} "Users"]]
   (when (nil? user) [:li [:a {:href "/users/sign-up"} [:span.label.success "Sign Up"]]])])

(defn- current-track-template [request]
  [:script#track-template {:type "text/example" }
    [:div.album-cover {:data-thumbnail "large" :data-title "{{ track.title}}" :data-artist "{{ track.artist }}" :data-album "{{ track.album }}"}
     [:a { :href "#" } [:img.thumbnail { :src "{{ track.artwork.large }}" }]]]
    [:div.meta-data
      [:h1.title "{{ track.title }}"]
      [:p.play-count "Play count: {{ track.playCount }}"]
      [:p.skip-count "Skip count: {{ track.skipCount }}"]
      [:p.owner "Owner: {{ track.owner }}"]
      [:p.requester "Requester: {{ track.requester }}"]
      [:p.artist "{{ track.artist }}"]
      [:p.album "{{ track.album }}"]]
    ])

(defn- player-controls-template [request]
  [:script#player-template {:type "text/example" }
   [:p.progress {:data-current "0" :data-duration "{{ track.duration }}"}
    [:span.remaining]]
   [:p.controls
    "<% if (track.playing) { %>"
    [:a.btn.play {:href "/player/pause" :data-remote "true"} "Pause"]
    "<% if (track.isRequester) { %>"
    [:a.btn.skip {:href "/player/skip" :data-remote "true"} "Skip"]
    "<% } } else { %>"
    [:a.btn.pause {:href "/player/play" :data-remote "true"} "Play"]
    "<% } %>"]])

(defn- playlist-template [request]
  [:script#playlist-template {:type "text/example" }
   "<% _.each(tracks, function(track) { %>"
   [:li
     [:div.meta-data
       [:h6.title "{{ track.title }}"]
       [:p.artist "{{ track.artist }}"]
       [:p.owner "Owner: {{ track.owner }}"]
       [:p.requester "Requester: {{ track.requester }}"]
       "<% if(track.isRequester) { %>"
         [:p
          [:a.delete-playlist-track {:href "/playlist/{{ track.id }}/delete" :data-remote "true" :data-method "DELETE"} "Delete"]]
        "<% } %>"
    "<% }); %>"]]])

(defn single-page []
  (html5
    [:head
      [:title "Jukebox2"]
        [:script {:src "/js/v/jquery-1.9.0.min.js"}]
        [:script {:src "/js/v/bootstrap-2.2.2.js"}]
        [:script {:src "/js/application.js"}]
        (include-css "/css/v/bootstrap-2.2.2.min.css")
        [:script {:src "/js/v/contentflow/contentflow.js" :type "text/javascript" :load "jukebox"}]
        [:script {:src "/js/v/plupload.full.js" :type "text/javascript"}]
        [:script {:src "/js/embed_plupload.js" :type "text/javascript"}]
        (include-css "/css/v/bootstrap-responsive-2.2.2.min.css")
        (include-css "/css/application.css")]
    [:body
      [:div.container-fluid
        [:div.row-fluid
            [:h3.pull-left "Jukebox 2"]
            [:div#user.pull-right]]
        [:div.row-fluid
          [:div#gutter.span2
            [:div.well
              [:ul#gutter-nav.nav.nav-list
                [:li.active [:a#now-playing {:href "#"} "Now Playing"]]
                [:li.divider]
                [:li.nav-header "Library"]
                [:li [:a#library-browse {:href "#"} "Browse"]]
                [:li [:a#library-upload {:href "#"} "Upload"]]
                [:li.divider]
                [:li.nav-header "Stats"]
                [:li [:a#stats-user {:href "#"} "Users"]]]]]
          [:div#main.span10
            [:div#content]]
         [:div#footer
          [:div#current-track
            [:img {:src "/img/no_art_lrg.png"}]]
          [:div#player-controls]]]
       [:div#modal-zone]
        (current-track-template nil)
        (player-controls-template nil)
        (playlist-template nil)]]))

(defn main [request title & content]
  (let [current-user (user/find-by-login (-> request :session :current-user))]
    (html5
      [:head
       [:title (str title " - jukebox2")]
       [:script {:src "http://use.typekit.com/ygg5mdb.js"}]
       [:script {:src "/js/v/jquery-1.6.4.min.js"}]
       [:script {:src "/js/v/underscore-min.js"}]
       [:script {:src "/js/jukebox.js"}]
       [:script {:src "/js/keyboard_shortcuts.js"}]
       [:script {:src "/js/progress.js"}]
       [:script {:src "/js/uploader.js"}]
       [:script {:src "/js/notifications.js"}]
       [:script {:src "/js/player.js"}]
       [:script {:src "/js/playlist.js"}]
       [:script {:src "/js/files.js"}]
       [:script {:src "/js/track_search.js"}]
       [:script {:src "/js/v/bootstrap-dropdown.js"}]
       (include-css "/css/v/bootstrap-1.3.0.min.css")]
       (include-css "/css/style.css")
      [:body {:data-accept "mp3|m4a|mp4|mpeg"}
       [:div.topbar
        [:div.fill
         [:div.container
          [:a.brand {:href "/"} "jukebox2"]
          (nav-links request current-user)
          (track-search/display-search request)
          (if (nil? current-user)
            (login-form)
            (logged-in current-user))]]]
       [:ul#notifications]
       [:div.container
        [:div.content
         (str
           (if (-> request :flash :success) (html [:div {:class "alert-message success"} (-> request :flash :success)]))
           (html content))]]
       (current-track-template request)
       (player-controls-template request)
       (playlist-template request)
       [:script#file-notification {:type "text/example" }
        [:li.uploading.alert-message.block-message
         [:p "{{ file.name }} {{ file.size }}mb"]
         [:div.progress-wrapper
          [:div.progress-bar]]]]])))
