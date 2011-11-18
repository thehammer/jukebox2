(ns jukebox-web.views.layout
  (:require [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers form-helpers]
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
       [:li (form-to [:post "/users/sign-out"]
                     (danger-submit-button "Sign Out"))]]]
     [:img {:src (user/avatar-url current-user {:s "37"})}]]])

(defn- nav-links [request]
  [:ul.nav
   [:li.dropdown {:data-dropdown "dropdown"}
    [:a.dropdown-toggle {:href "#"} "Add"]
    [:ul.dropdown-menu
     [:li [:a#random {:href "/playlist/add-one" :data-remote "true"} "Random"]]
     [:li [:a {:href "/library/browse"} "From Library"]]]]
   [:li [:a {:href "/library/most-played"} "Most Played"]]
   [:li [:a {:href "/users"} "Users"]]
   [:li [:a {:href "/hammertimes"} "Hammertimes"]]
   (when (nil? (-> request :session :current-user)) [:li [:a {:href "/users/sign-up"} [:span.label.success "Sign Up"]]])])

(defn- current-track-template [request]
  [:script#track-template {:type "text/example" }
   [:h1.title "{{ track.title }}"]
   [:p.artist "{{ track.artist }}"]
   [:p.album "{{ track.album }}"]
   [:p.play-count "Play count: {{ track.playCount }}"]])

(defn- playlist-template [request]
  [:script#playlist-template {:type "text/example" }
   "<% _.each(tracks, function(track) { %>"
   [:li
     [:div.meta-data
       [:h6.title "{{ track.title }}"]
       [:p.artist "{{ track.artist }}"]]]
    "<% }); %>"])

(defn- player-controls-template [request]
  [:script#player-template {:type "text/example" }
   [:p.progress {:data-current "0" :data-duration "{{ track.duration }}"}
    [:span.remaining]]
   [:p.controls
    "<% if (track.playing) { %>"
    [:a.btn.play {:href "/player/pause" :data-remote "true"} "Pause"]
    "<% if (track.canSkip) { %>"
    [:a.btn.skip {:href "/player/skip" :data-remote "true"} "Skip"]
    "<% } } else { %>"
    [:a.btn.pause {:href "/player/play" :data-remote "true"} "Play"]
    "<% } %>"]])

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
       [:script {:src "/js/artwork.js"}]
       [:script {:src "/js/player.js"}]
       [:script {:src "/js/playlist.js"}]
       [:script {:src "/js/files.js"}]
       [:script {:src "/js/v/bootstrap-dropdown.js"}]
       (include-css "/css/v/bootstrap-1.3.0.min.css")]
       (include-css "/css/style.css")
      [:body {:data-accept "mp3|m4a|mp4|mpeg"}
       [:div.topbar
        [:div.fill
         [:div.container
          [:a.brand {:href "/"} "jukebox2"]
          (nav-links request)
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
