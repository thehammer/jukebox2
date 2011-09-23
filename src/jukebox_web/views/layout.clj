(ns jukebox-web.views.layout
  (:require [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers form-helpers]))

(defn main [request title & content]
  (let [current-user (user/find-by-login (-> request :session :current-user))]
    (html5
      [:head
       [:title title]
       [:script {:src "/js/jquery-1.6.4.min.js"}]
       [:script {:src "/js/jquery.plupload.queue.js"}]
       [:script {:src "/js/plupload.full.js"}]
       [:script {:src "/js/upload.js"}]
       [:script {:src "http://twitter.github.com/bootstrap/1.3.0/bootstrap-dropdown.js"}]
       [:script {:src "/js/application.js"}]
       (include-css "http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css")]
       (include-css "/css/jquery.plupload.queue.css")
       (include-css "/css/style.css")
      [:body
       [:div.topbar
        [:div.fill
         [:div.container
          [:a.brand {:href "/"} "jukebox2"]
          [:ul.nav
           [:li.dropdown {:data-dropdown "dropdown"}
            [:a.dropdown-toggle {:href "#"} "Add"]
            [:ul.dropdown-menu
             [:li [:a {:href "/playlist/add-one"} "Random"]]
             [:li [:a {:href "/library/browse"} "From Library"]]]]
           [:li [:a {:href "/users"} "Users"]]
           [:li [:a {:href "/hammertimes"} "Hammertimes"]]]
          (if (nil? current-user)
            [:form.pull-right {:method :post :action "/users/authenticate"}
             [:input.input-small {:type "text" :placeholder "login" :name "login"}]
             [:input.input-small {:type "password" :placeholder "password" :name "password"}]
             [:button.btn {:type "submit"} "Sign In"]]
            [:div.pull-right.logged-in
              [:p "you are logged in as " [:strong (:login current-user)]]
              (form-to [:post "/users/sign-out"] (submit-button "Sign Out"))]
            )]]]
       [:div.container
        [:div.content
         [:div.page-header
          [:h1 "jukebox2"]]
         content]]])))
