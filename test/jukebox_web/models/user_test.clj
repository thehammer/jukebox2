(ns jukebox-web.models.user-test
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.factory :as factory])
  (:use [clojure.test]
        [jukebox-web.test-helper])
  (:import [org.mindrot.jbcrypt BCrypt]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest sign-up-stores-user
  (let [[user errors] (user/sign-up! (factory/user {:login "hammer" :avatar "http://gravitar.org/somepic"}))
        hammer (user/find-by-login "hammer")]
    (is (empty? errors))
    (is (= "http://gravitar.org/somepic" (:avatar hammer)))))
;
(deftest sign-up-encrypts-password-before-storing
  (let [[hammer errors] (user/sign-up! (factory/user {:password "pass" :password-confirmation "pass"}))
        hashed-password (:password hammer)]
    (is (BCrypt/checkpw "pass" hashed-password))))

(deftest sign-up-sets-users-skip-count-to-zero
  (user/sign-up! (factory/user {:login "test"}))
  (is (= 0 (:skip_count (user/find-by-login "test")))))

(deftest sign-up-does-not-store-confirmation
  (user/sign-up! (factory/user {:login "test" :password "pass" :password-confirmation "pass"}))
  (is (nil? (:password-confirmation (user/find-by-login "test")))))

(deftest sign-up-enables-user
  (user/sign-up! (factory/user {:login "test"}))
  (is (:enabled (user/find-by-login "test"))))

(deftest returns-errors-if-invalid
  (let [[user errors] (user/sign-up! {})]
    (is (not (empty? errors)))
    (is (= ["is required"] (:login errors)))))

(deftest tracks-have-request
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (let [user (user/find-by-login "user")
        track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3")
                               (:login user) "")]
    (is (user/isRequester? track user))))

(deftest user-is-requester-if-track-requested-by-randomizer ; FIXME: seems strange
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (let [user (user/find-by-login "user")
        track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login "(randomizer)"} "")]
    (is (user/isRequester? track user))))

(deftest user-is-not-requester-if-not-logged-in
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (let [user (user/find-by-login "user")
        track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login user} "")]
    (is (not (user/isRequester? track nil)))))

(deftest user-is-not-requester-if-someone-else-requested-track
  (user/sign-up! (factory/user {:login "user"}))
  (user/sign-up! (factory/user {:login "user2"}))
  (let [user (user/find-by-login "user2")
        track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login "user"} "")]
    (is (not (user/isRequester? track user)))))

(deftest validates-users
  (testing "requires a login"
    (let [errors (user/validate {:password "" :avatar "http://foo.com"})]
      (is (= ["is required"] (:login errors)))))

  (testing "does not require an avatar, as a default will be used"
    (let [errors (user/validate {:login "foo" :password ""})]
      (is (nil? (:avatar errors)))))

  (testing "requires a password"
    (let [errors (user/validate {:login "foo" :avatar "http://foo.com"})]
      (is (= ["is required"] (:password errors))))))

(deftest validates-users-for-sign-up
  (testing "requires the password and confirmation to match"
    (let [errors (user/validate-for-sign-up (factory/user {:password-confirmation "different"}))]
      (is (= ["does not match"] (:password-confirmation errors)))))

  (testing "requires a non-reserved name"
    (let [errors (user/validate-for-sign-up (factory/user {:login "(randomizer)"}))]
      (is (= '("can't be a reserved name") (:login errors))))))

(deftest sign-up-requires-unique-login
  (user/sign-up! (factory/user {}))
  (let [errors (user/validate-for-sign-up (factory/user {}))]
    (is (= ["must be unique"] (:login errors)))))

(deftest autheticates-users-if-credentials-are-valid
  (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmation "p"}))
  (is (user/authenticate "a" "p")))

(deftest does-not-authenticate-if-credentials-are-invalid
  (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmatoin "p"}))
  (is (not (user/authenticate "a" "wrong"))))

(deftest does-not-authenticate-if-user-does-not-exist
  (is (not (user/authenticate "bad_login" "wrong"))))

(deftest increment-skip-count-increments-the-skip-count-for-the-given-user
    (user/sign-up! (factory/user {:login "test"}))
    (is (= 0 (:skip_count (user/find-by-login "test"))))
    (user/increment-skip-count! "test")
    (is (= 1 (:skip_count (user/find-by-login "test")))))

(deftest increment-skip-count-increments-multiple-skips
    (user/sign-up! (factory/user {:login "test"}))
    (user/increment-skip-count! "test")
    (user/increment-skip-count! "test")
    (is (= 2 (:skip_count (user/find-by-login "test")))))

(deftest update-updates-the-user
  (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
  (let [user (user/find-by-login "avatar-test")]
    (user/update! user {:avatar "new-avatar.png"})
    (is (= "new-avatar.png" (:avatar (user/find-by-login "avatar-test"))))))

(deftest update-merges-user-args-with-current-attributes-when-running-validations
  (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
  (let [user (user/find-by-login "avatar-test")]
    (user/update! user {:avatar "new-avatar.png"})
    (is (= "new-avatar.png" (:avatar (user/find-by-login "avatar-test"))))))

(deftest update-returns-errors-if-validations-fail
  (let [errors (user/update! (factory/user {}) {:login ""})]
    (is (= ["is required"] (:login errors)))))

(deftest delete-deletes-the-user
  (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
  (let [test-user (user/find-by-login "avatar-test")]
    (user/delete! test-user)
    (let [deleted-user (user/find-by-login "avatar-test")]
      (is (= nil deleted-user)))))

(deftest find-all-returns-all-the-users
    (user/sign-up! (factory/user {}))
    (user/sign-up! (factory/user {:login "kenny"}))
    (is (= 2 (count (user/find-all)))))

(deftest find-enabled-returns-enabled-users
  (user/sign-up! (factory/user {:login "kyle"}))
  (user/sign-up! (factory/user {:login "kenny"}))
  (user/toggle-enabled! "kyle")
  (let [enabled-users (map #(:login %) (user/find-enabled))]
    (is (some #{"kenny"} enabled-users))
    (is (not (some #{"kyle"} enabled-users)))))

(deftest enabled-is-false-when-the-user-does-not-exist
  (is (not (user/enabled? "missing"))))

(deftest toggle-enabled-enables-a-disabled-user
  (user/sign-up! (factory/user {:login "test" :enabled false}))
  (user/toggle-enabled! "test")
  (is (user/enabled? "test")))

(deftest toggle-enabled-disables-an-enabled-user
  (user/sign-up! (factory/user {:login "test" :enabled true}))
  (is (user/enabled? "test"))
  (user/toggle-enabled! "test")
  (is (not (user/enabled? "test"))))

(deftest count-songs-returns-0-for-a-new-user
  (let [[user errors] (user/sign-up! (factory/user {:login "newuser" :enabled false}))]
    (is (= 0 (user/count-songs user)))))

(deftest count-songs-returns-3-for-a-user-with-3-songs
  (let [[user errors] (user/sign-up! (factory/user {:login "user" :enabled false}))]
    (is (= 3 (user/count-songs user)))))

(deftest base-avatar-url-returns-the-default-url-for-user-without-avatar
  (let [user (factory/user {:avatar ""})]
    (is (= "http://www.gravatar.com/avatar/no-avatar" (user/base-avatar-url user)))))

(deftest base-avatar-returns-stored-avatar-if-the-user-has-one
  (let [user (factory/user {:avatar "http://www.gravatar.com/me.png"})]
    (is (= "http://www.gravatar.com/me.png" (user/base-avatar-url user)))))

(deftest avatar-url-returns-the-default-url
  (let [user (factory/user {:avatar ""})]
    (is (= "http://www.gravatar.com/avatar/no-avatar?s=35&d=mm" (user/avatar-url user)))))

(deftest avatar-url-allows-size-to-be-specified
  (let [user (factory/user {:avatar "http://www.gravatar.com/me.png"})
        url (user/avatar-url user {:s 1234})]
    (is (= "http://www.gravatar.com/me.png?s=1234&d=mm" url))))
