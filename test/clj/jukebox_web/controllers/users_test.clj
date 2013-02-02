(ns jukebox-web.controllers.users-test
  (:require [cheshire.core :as json])
  (:require [jukebox-web.controllers.users :as users-controller])
  (:require [jukebox-web.models.user :as user])
  (:require [jukebox-web.models.factory :as factory])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection)

(deftest authenticates-users-successfully
    (user/sign-up! (factory/user {:login "bob" :password "pass" :password-confirmation "pass"}))
    (let [request {:params {:login "bob" :password "pass"}}
          response (users-controller/authenticate request)]
      (testing "redirects to the playlist if credentials are correct"
        (is (= 302 (:status response)))
        (is (= {"Location" "/playlist"} (:headers response))))
      (testing "sets the current user in the session"
        (is (= "bob" (-> response :session :current-user))))))

(deftest authenticates-users-successfully
  (testing "session is empty if the credentials are incorrect"
    (user/sign-up! (factory/user {:login "bob" :password "pass" :password-confirmation "pass"}))
    (let [request {:params {:login "bob" :password "fat-finger"}}
          response (users-controller/authenticate request)]
      (is (nil? (-> response :session :current-user))))))

(deftest edit-users
  (testing "renders successfully"
    (let [[user errors] (user/sign-up! (factory/user {:login "test-edit"}))
          request {:params {:id (:id user)}}
          response (users-controller/edit request)]
      (is (.contains response "Edit test-edit")))))

(deftest update-users
  (testing "updates the user and redirects"
    (let [[user errors] (user/sign-up! (factory/user {:login "test-update" :avatar "old-avatar.png"}))
          request {:params {:id (:id user) :avatar "new-avatar.png"}}
          response (users-controller/update request)]
      (is (= 302 (:status response)))
      (is (= {"Location" "/users"} (:headers response)))
      (is (= "new-avatar.png" (:avatar (user/find-by-login "test-update")))))))

(deftest create-new-users-successfully
  (let [request {:params (factory/user {:login "test"})}
        response (users-controller/sign-up request)]
    (testing "saves a valid user"
      (is (not (nil? (user/find-by-login "test")))))

    (testing "redirects valid requests to the playlist"
      (is (= 302 (:status response)))
      (is (= {"Location" "/playlist"} (:headers response))))

    (testing "logs you in when you sign up"
      (is (= "test" (-> response :session :current-user))))))

(deftest create-new-users-unsuccessfully
  (testing "rerenders the sign-up page if the user is invalid"
    (let [request {:params (factory/user {:login ""})}
          response (users-controller/sign-up request)]
      (is (nil? (:status response)))
      (is (nil? (:headers response))))))

(deftest sign-out-users
  (user/sign-up! (factory/user {:login "bob" :password "pass"}))
  (users-controller/authenticate {:params {:login "bob" :password "pass"}})

  (let [response (users-controller/sign-out {})]
    (testing "redirects to the playlist"
      (is (= 302 (:status response)))
      (is (= {"Location" "/playlist"} (:headers response))))

    (testing "removes the current user from the session"
      (is (nil? (-> response :session :current-user))))))

(deftest toggles-enabled
  (testing "redirects back to users index"
    (user/sign-up! (factory/user {:login "test"}))
    (let [response (users-controller/toggle-enabled {:params {:login "test"}})]
      (is (= 302 (:status response)))
      (is (= {"Location" "/users"} (:headers response))))))

(deftest signing-up-successfully
  (let [request {:params (factory/user {:login "test"})}
        response (users-controller/sign-up-api request)]
    (testing "is a successful response"
      (is (= 200 (-> response :status))))
    (testing "logs the user in"
      (is (= "test" (-> response :session :current-user))))
    (testing "returns the user"
      (is (= "test" (get (json/parse-string (:body response)) "login"))))))

(deftest signing-up-unsuccessfully
  (let [request {:params (factory/user {:password "x" :password-confirmation "y"})}
        response (users-controller/sign-up-api request)]
    (testing "is an unprocessable entity"
      (is (= 422 (-> response :status))))
    (testing "does not log the user in"
      (is (nil? (-> response :session :current-user))))
    (testing "returns the errors"
      (is (= ["does not match"] (get (json/parse-string (:body response)) "password-confirmation"))))))

(deftest signing-in-successfully
  (user/sign-up! (factory/user {:login "test" :password "pw" :password-confirmation "pw"}))
  (let [request {:params {:login "test" :password "pw"}}
        response (users-controller/sign-in-api request)]
    (testing "is a successful response"
      (is (= 200 (-> response :status))))
    (testing "logs the user in"
      (is (= "test" (-> response :session :current-user))))
    (testing "returns the user"
      (is (= "test" (get (json/parse-string (:body response)) "login"))))))

(deftest signing-in-unsuccessfully
  (user/sign-up! (factory/user {:login "test" :password "pw" :password-confirmation "pw"}))
  (let [request {:params {:login "test" :password "bad"}}
        response (users-controller/sign-in-api request)]
    (testing "is an unprocessable entity"
      (is (= 403 (-> response :status))))
    (testing "does not log the user in"
      (is (nil? (-> response :session :current-user))))
    (testing "returns the errors"
      (is (= ["does not match"] (get (json/parse-string (:body response)) "password"))))))
