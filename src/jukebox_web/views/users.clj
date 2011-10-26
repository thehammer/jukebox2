(ns jukebox-web.views.users
  (:require [jukebox-web.views.layout :as layout]
            [jukebox-web.models.user :as user])
  (:use [hiccup core page-helpers form-helpers]
        [jukebox-web.views.forms]))

(defn sign-up [request errors]
  (layout/main request "Sign Up"
    (form-to [:post "/users/sign-up"]
      (labeled-field text-field :login "Login" errors)
      (labeled-field password-field :password "Password" errors)
      (labeled-field password-field :password-confirmation "Password Confirmation" errors)
      (labeled-field text-field :avatar "Avatar" errors)
      [:div (submit-button "Sign Up")])))

(defn- show-user [user]
  [:tr
    [:td [:img {:src (:avatar user)}]]
    [:td [:span (:login user)]]
    [:td [:span (user/count-songs user)]]
    [:td [:span (:skip-count user)]]
    [:td
     (form-to [:post "/users/toggle-enabled"]
       (hidden-field "login" (:login user))
       (submit-button (if (:enabled user) "Disable" "Enable")))
     [:a {:href (str "/users/" (:id user) "/edit") :class "btn"} "Edit"]]])

(defn index [request users]
  (layout/main request "Users"
    [:table
      [:tr [:th "Avatar"] [:th "Login"] [:th "Songs"] [:th "Skips"] [:th "Actions"]]
      (map show-user users)]))

(defn edit [request user errors]
  (layout/main request (str "Edit User: " (:login user))
    [:h2 (str "Edit " (:login user))
      (form-to [:post (str "/users/" (:id user) "/update")]
        (labeled-field text-field :avatar "Avatar" (or (-> request :params :avatar) (:avatar user)) errors)
        [:div (submit-button "Update")])]))
