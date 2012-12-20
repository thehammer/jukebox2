(ns jukebox-web.models.user-spec
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist-track :as playlist-track]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper])
  (:import [org.mindrot.jbcrypt BCrypt]))

(describe "sign-up!"
  (with-database-connection)

  (it "stores a new user"
    (let [[user errors] (user/sign-up! (factory/user {:login "hammer" :avatar "http://gravitar.org/somepic"}))
          hammer (user/find-by-login "hammer")]
      (should (empty? errors))
      (should= "http://gravitar.org/somepic" (:avatar hammer))))

  (it "encrypts the password before storing"
    (let [[hammer errors] (user/sign-up! (factory/user {:password "pass" :password-confirmation "pass"}))
          hashed-password (:password hammer)]
      (should (BCrypt/checkpw "pass" hashed-password))))

  (it "encrypts the password before storing"
    (let [[hammer errors] (user/sign-up! (factory/user {:password "pass" :password-confirmation "pass"}))
          hashed-password (:password hammer)]
      (should (BCrypt/checkpw "pass" hashed-password))))

  (it "sets the users skip-count to zero"
    (user/sign-up! (factory/user {:login "test"}))
    (should= 0 (:skip-count (user/find-by-login "test"))))

  (it "doesnt store password confirmation"
    (user/sign-up! (factory/user {:login "test" :password "pass" :password-confirmation "pass"}))
    (should=  nil (:password-confirmation (user/find-by-login "test"))))

  (it "enables the user"
    (user/sign-up! (factory/user {:login "test"}))
    (should (:enabled (user/find-by-login "test"))))

  (it "returns errors if the user is not valid"
    (let [[user errors] (user/sign-up! {})]
      (should-not (empty? errors))
      (should= ["is required"] (:login errors)))))

(describe "isRequester?"
  (with-test-music-library)
  (with-database-connection)

  (before
    (user/sign-up! (factory/user {:login "user"}))
    (user/sign-up! (factory/user {:login "user2"})))

  (it "allows skips for the requesting user"
    (let [user (user/find-by-login "user")
          track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3")
                                 (:login user) "")]
      (should (user/isRequester? track user))))

  (it "allows skips if the requesting user is randomizer"
    (let [user (user/find-by-login "user")
          track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login "(randomizer)"} "")]
      (should (user/isRequester? track user))))

  (it "prevents skips if you're not logged in"
    (let [user (user/find-by-login "user")
          track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login user} "")]
      (should-not (user/isRequester? track nil))))

  (it "prevents skips if you're not the request user"
    (let [user (user/find-by-login "user2")
          track (playlist-track/new-playlist-track (library/file-on-disk "user/artist/album/track.mp3") {:login "user"} "")]
      (should-not (user/isRequester? track user)))))

(describe "validate"
  (with-database-connection)

  (it "requires a login"
    (let [errors (user/validate {:password "" :avatar "http://foo.com"})]
      (should= ["is required"] (:login errors))))

  (it "does not require an avatar, as a default will be used"
    (let [errors (user/validate {:login "foo" :password ""})]
      (should= nil (:avatar errors))))

  (it "requires a password"
    (let [errors (user/validate {:login "foo" :avatar "http://foo.com"})]
      (should= ["is required"] (:password errors)))))

(describe "validate-for-sign-up"
  (with-database-connection)

  (it "requires the password and confirmation to match"
    (let [errors (user/validate-for-sign-up (factory/user {:password-confirmation "different"}))]
      (should= ["does not match"] (:password-confirmation errors))))

  (it "requires a non-reserved name"
    (let [errors (user/validate-for-sign-up (factory/user {:login "(randomizer)"}))]
      (should= '("can't be a reserved name") (:login errors))))

  (it "requires unique login on sign up"
    (user/sign-up! (factory/user {}))
    (let [errors (user/validate-for-sign-up (factory/user {}))]
      (should= ["must be unique"] (:login errors)))))

(describe "authenticate"
  (with-database-connection)

  (it "returns true if credentials are valid"
    (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmation "p"}))
    (should (user/authenticate "a" "p")))

  (it "returns false if credentials are invalid"
    (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmatoin "p"}))
    (should-not (user/authenticate "a" "wrong")))

  (it "returns false if login is invalid"
    (should-not (user/authenticate "bad_login" "wrong"))))

(describe "increment-skip-count"
  (with-database-connection)

  (it "increments the skip count for the given user"
    (user/sign-up! (factory/user {:login "test"}))
    (should= 0 (:skip-count (user/find-by-login "test")))
    (user/increment-skip-count! "test")
    (should= 1 (:skip-count (user/find-by-login "test"))))

  (it "increments multiple skips"
    (user/sign-up! (factory/user {:login "test"}))
    (user/increment-skip-count! "test")
    (user/increment-skip-count! "test")
    (should= 2 (:skip-count (user/find-by-login "test")))))

(describe "update!"
  (with-database-connection)

  (it "updates the user"
    (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
    (let [user (user/find-by-login "avatar-test")]
      (user/update! user {:avatar "new-avatar.png"})
      (should= "new-avatar.png" (:avatar (user/find-by-login "avatar-test")))))

  (it "merges user-args with current attributes when running validations"
    (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
    (let [user (user/find-by-login "avatar-test")]
      (user/update! user {:avatar "new-avatar.png"})
      (should= "new-avatar.png" (:avatar (user/find-by-login "avatar-test")))))

  (it "returns errors if validations fail"
    (let [errors (user/update! (factory/user {}) {:login ""})]
      (should= ["is required"] (:login errors)))))

(describe "delete!"
  (with-database-connection)

  (it "deletes the user"
    (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
    (let [test-user (user/find-by-login "avatar-test")]
      (user/delete! test-user)
      (let [deleted-user (user/find-by-login "avatar-test")]
        (should= nil deleted-user)))))

(describe "find-all"
  (with-database-connection)

  (it "returns all the users"
    (user/sign-up! (factory/user {}))
    (user/sign-up! (factory/user {:login "kenny"}))
    (should= 2 (count (user/find-all)))))

(describe "find-enabled"
  (with-database-connection)

  (it "returns enabled users"
    (user/sign-up! (factory/user {:login "kyle"}))
    (user/sign-up! (factory/user {:login "kenny"}))
    (user/toggle-enabled! "kyle")
    (let [enabled-users (map #(:login %) (user/find-enabled))]
      (should (some #{"kenny"} enabled-users))
      (should-not (some #{"kyle"} enabled-users)))))

(describe "enabled?"
  (with-database-connection)

  (it "is false when the user does not exist"
    (should-not (user/enabled? "missing"))))

(describe "toggle-enabled"
  (with-database-connection)

  (it "enables a disabled user"
    (user/sign-up! (factory/user {:login "test" :enabled false}))
    (user/toggle-enabled! "test")
    (should (user/enabled? "test")))

  (it "disables an enabled user"
    (user/sign-up! (factory/user {:login "test" :enabled true}))
    (user/toggle-enabled! "test")
    (should-not (user/enabled? "test"))))

(describe "count-songs"
  (with-test-music-library)
  (with-database-connection)

  (it "returns 0 for a new user"
    (let [[user errors] (user/sign-up! (factory/user {:login "newuser" :enabled false}))]
      (should= 0 (user/count-songs user))))

  (it "returns 3 for a user with 3 songs"
    (let [[user errors] (user/sign-up! (factory/user {:login "user" :enabled false}))]
      (should= 3 (user/count-songs user)))))

(describe "base-avatar-url"
  (it "returns the default url user without an avatar"
    (let [user (factory/user {:avatar ""})]
      (should= "http://www.gravatar.com/avatar/no-avatar" (user/base-avatar-url user))))

  (it "returns the stored avatar if the user has one"
    (let [user (factory/user {:avatar "http://www.gravatar.com/me.png"})]
      (should= "http://www.gravatar.com/me.png" (user/base-avatar-url user)))))

(describe "avatar-url"
  (it "returns the default url with default query params when called with one arg"
    (let [user (factory/user {:avatar ""})]
      (should= "http://www.gravatar.com/avatar/no-avatar?s=35&d=mm" (user/avatar-url user))))

  (it "allows size to be specified via the params argument"
    (let [user (factory/user {:avatar "http://www.gravatar.com/me.png"})
          url (user/avatar-url user {:s 1234})]
      (should= "http://www.gravatar.com/me.png?s=1234&d=mm" url))))

(run-specs)
