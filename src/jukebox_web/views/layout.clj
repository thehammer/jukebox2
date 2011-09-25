(ns jukebox-web.views.layout
  (:require [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers form-helpers]))

(defn- login-form []
  [:div.pull-right
   [:ul
    [:li.dropdown {:data-dropdown "dropdown"}
     [:a.dropdown-toggle "would you like to log in?"]
     [:ul.dropdown-menu
      [:form.login {:method :post :action "/users/authenticate"}
       [:input.input-small {:type "text" :placeholder "login" :name "login"}]
       [:input.input-small {:type "password" :placeholder "password" :name "password"}]
       [:input.btn.success {:type "submit" :value "Sign In"}]]]]]])

(defn- logged-in [current-user]
  [:div.pull-right.logged-in
    [:ul
     [:li.dropdown {:data-dropdown "dropdown"}
      [:a.dropdown-toggle "this is you " (:login current-user) " " ]
      [:ul.dropdown-menu
       [:li (form-to [:post "/users/sign-out"]
                     (submit-button "Sign Out"))]]]
     [:img {:src (str (:avatar current-user) "?s=37")}]]])

(defn- nav-links []
  [:ul.nav
   [:li.dropdown {:data-dropdown "dropdown"}
    [:a.dropdown-toggle {:href "#"} "Add"]
    [:ul.dropdown-menu
     [:li [:a {:href "/playlist/add-one"} "Random"]]
     [:li [:a {:href "/library/browse"} "From Library"]]]]
   [:li [:a {:href "/users"} "Users"]]
   [:li [:a {:href "/hammertimes"} "Hammertimes"]]])

(defn main [request title & content]
  (let [current-user (user/find-by-login (-> request :session :current-user))]
    (html5
      [:head
       [:title title]
       [:script {:src "/js/jquery-1.6.4.min.js"}]
       [:script {:src "/js/underscore-min.js"}]
       [:script {:src "/js/uploader.js"}]
       [:script {:src "/js/notifications.js"}]
       [:script {:src "/js/files.js"}]
       [:script {:src "http://twitter.github.com/bootstrap/1.3.0/bootstrap-dropdown.js"}]
       [:script {:src "/js/application.js"}]
       (include-css "http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css")]
       (include-css "/css/jquery.plupload.queue.css")
       (include-css "/css/style.css")
      [:body {:data-accept "mp3|m4a|mp4"}
       [:div.topbar
        [:div.fill
         [:div.container
          [:a.brand {:href "/"} "jukebox2"]
          (nav-links)
          (if (nil? current-user)
            (login-form)
            (logged-in current-user))]]]
       [:ul#notifications]
       [:div.container
        [:div.content
         content]]
       [:script#file-notification {:type "text/example" }
        [:li.uploading
         [:p "<%= file.name %> <%= file.size %>mb"]
         [:div.progress-wrapper
          [:div.progress-bar]]]]])))
