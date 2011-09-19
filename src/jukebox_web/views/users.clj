(ns jukebox-web.views.users
  (:require [jukebox-web.views.layout :as layout])
  (:use [hiccup core page-helpers form-helpers]))

(defn sign-in []
  (layout/main "Sign In"
    (form-to [:post "/users/authenticate"]
      (label "login" "Login")
      (text-field "login")
      (label "password" "Password")
      (password-field "password")
      (submit-button "Sign In"))))

(defn- labeled-field [field-type id label-text errors]
  [:div
    (label id label-text)
    (field-type id)
    [:span (id errors)]])

(defn sign-up [errors]
  (layout/main "Sign Up"
    (form-to [:post "/users/sign-up"]
      (labeled-field text-field :login "Login" errors)
      (labeled-field password-field :password "Password" errors)
      (labeled-field password-field :password_confirmation "Password Confirmation" errors)
      (labeled-field text-field :avatar "Avatar" errors)
      [:div (submit-button "Sign Up")])))

(defn- show-user [user]
  [:tr
    [:td [:img {:src (:avatar user)}]]
    [:td [:span (:login user)]]
    [:td [:span (:skip-count user)]]])

(defn index [users]
  (layout/main "Users"
    [:table
      [:tr [:th] [:th "Login"] [:th "Skips"]]
      (map show-user users)]))
