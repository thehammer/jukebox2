(ns jukebox-web.models.library-test
  (:require [clojure.java.io :as io]
            [jukebox-web.util.encoding :as enc]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.library :as library])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest uploading-files-saves-metadata
  (let [file "test/clj/fixtures/music/jukebox2.tmp"
        [user _] (user/sign-up! (factory/user {:login "test"}))
        track (library/save-file! file "jukebox2.mp3" user)]
    (testing "returns saved track metadata"
      (is (= track (library/find-by-id (:id track)))))
    (testing "metadata has tags"
      (is (= "Hammer" (:artist track)))
      (is (= "Hammer's Album" (:album track)))
      (is (= "jukebox2" (:title track))))
      (is (= 16 (:duration_secs track)))
    (testing "metadata defaults counts"
      (is (zero? (:play_count track)))
      (is (zero? (:skip_count track))))
    (testing "looks up album art"
      (is (= "/img/no_art_lrg.png" (:large_image track)))
      (is (= "/img/no_art_lrg.png" (:xlarge_image track))))))

(deftest uploading-files-makes-the-user-the-owner
  (let [file "test/clj/fixtures/music/jukebox2.tmp"
        [user _] (user/sign-up! (factory/user {:login "test"}))
        track (library/save-file! file "jukebox2.mp3" user)]
    (is (= user (library/owner-md track)))))

(deftest uploading-files-stores-in-pool
  (let [file "test/clj/fixtures/music/jukebox2.tmp"
        [user _] (user/sign-up! (factory/user {:login "test"}))
        track (library/save-file! file "jukebox2.mp3" user)]
    (testing "copies file to the pool"
      (is (not (= file (:location track))))
      (is (= (enc/sha256 (slurp file))
             (enc/sha256 (slurp (library/file-on-disk track))))))
    (testing "pool is in the *music-library* with the right extension"
      (is (.exists (library/file-on-disk track)))
      (is (.endsWith (:location track) ".mp3")))))

(deftest nested-location-keeps-too-many-files-from-being-in-one-directory
  (is (= "a/b/c/d/e/abcdefg"
         (library/nested-location "abcdefg"))))

;(deftest listing-a-directory
;  (testing "returns a seq with the files contained in the root path with no arguments"
;    (let [files (map #(.getPath %) (library/list-directory))]
;      (is (not (some (partial = "jukebox2.ogg") files)))
;      (is (some (partial = "jukebox2.mp3") files))))
;
;  (testing "returns the list of files for a given directory"
;    (let [files (map #(.getPath %) (library/list-directory "user"))]
;      (is (some (partial = "user/artist") files))))
;
;  (testing "does not include dotfiles in the list"
;    (let [files (map #(.getPath %) (library/list-directory))]
;      (is (not (some (partial = ".gitkeep") files))))))
;
;(deftest finding-parent-directory
;  (testing "returns nil when path is the music library"
;    (is (= nil (library/parent-directory ""))))
;
;  (testing "returns the root when its the user"
;    (is (= "" (library/parent-directory "user"))))
;
;  (testing "returns the user when in the artist directory"
;    (is (= "user" (library/parent-directory "user/artist"))))
;
;  (testing "returns url encoded values"
;    (is (= "daft%20punk" (library/parent-directory "daft punk/discovery"))))
;
;  (testing "returns url encoded values correctly for subdirectories"
;    (is (= "user%2Fdaft%20punk" (library/parent-directory "user/daft punk/discovery")))))
;


(deftest finding-all-artists
  (testing "returns all artists"
    (let [artists (library/all-artists)]
      (is (some (partial = "artist") (map :artist artists))))))

(deftest finding-all-albums-for-artist
  (testing  "returns all albums for artist"
    (let [albums (library/albums-for-artist "artist")]
      (is (some (partial = "album") (map :album albums))))))

(deftest finding-all-tracks-for-artists-album
  (testing "returns all track for a given artist's album"
    (let [tracks (library/tracks-for-artists-album "artist" "album")]
      (is (some (partial = "track") (map :title tracks))))))

(deftest counting-tracks-in-library
  (is (= 4 (library/count-tracks))))

(deftest counting-tracks-owned-by-user
  (is (= 3 (library/count-tracks-owned-by (user/find-by-login "user"))))
  (is (= 1 (library/count-tracks-owned-by (user/find-by-login "user2"))))
  (is (= 0 (library/count-tracks-owned-by (user/find-by-login "missing")))))

(deftest find-random-song
  (testing "returns a random song for any user"
    (is (not (nil? (library/random-song)))))

  (testing "returns a randome song for the given user"
    (let [user (user/find-by-login "user")
          selections (take 10 (map library/random-song (repeat user)))]
      (is (not (some (partial = "test/clj/fixtures/music/user2/artist2/album/track.mp3")
                     (map :tempfile_location selections))))))

  (testing "works for users with only one song"
    (let [user (user/find-by-login "user2")
          selections (take 10 (map library/random-song (repeat user)))]
      (print "test results: ")
      (prn (doall (map :tempfile_location selections)))
      (is (every? (partial = "test/clj/fixtures/music/user2/artist2/album/track.mp3")
                  (map :tempfile_location selections))))))

;(deftest owners
;  (testing "returns nil for a path without a user"
;    (let [track (io/file library/*music-library* "jukebox2.mp3")]
;      (is (nil? (library/owner track)))))
;
;  (testing "returns the login for a path with a user"
;    (let [track (io/file library/*music-library* "user/artist/album/jukebox2.mp3")]
;      (is (= "user" (library/owner track))))))
;
;(deftest play-count-returns-0-for-a-new-track
;  (is (= 0 (library/play-count (library/random-song)))))
;
;(deftest play-count-is-incremented
;  (let [track (library/random-song)]
;    (library/increment-play-count! track)
;    (is (= 1 (library/play-count track)))
;    (library/increment-play-count! track)
;    (is (= 2 (library/play-count track)))))
;
;(deftest skip-count-return-zero-for-new-track
;  (is (= 0 (library/skip-count (library/random-song)))))
;
;(deftest can-increment-skip-count
;  (let [track (library/random-song)]
;    (library/increment-skip-count! track)
;    (is (= 1 (library/skip-count track)))
;    (library/increment-skip-count! track)
;    (is (= 2 (library/skip-count track)))))
;
;(deftest can-return-most-played-songs-in-descending-order
;  (dotimes [_ 3] (library/increment-play-count! "three"))
;  (dotimes [_ 1] (library/increment-play-count! "one"))
;  (dotimes [_ 2] (library/increment-play-count! "two"))
;  (let [most-played (library/most-played)]
;    (is (= 3 (count most-played)))
;    (is (= "three" (:track (nth most-played 0))))
;    (is (= "two"   (:track (nth most-played 1))))
;    (is (= "one"   (:track (nth most-played 2))))))
;
;(deftest returns-only-the-20-most-played-tracks
;  (dotimes [n 21] (library/increment-play-count! n))
;  (is (= 20 (count (library/most-played)))))
;
;(deftest finding-artists
;  (testing "returns nil for a path without an artist"
;    (let [track (io/file library/*music-library* "jukebox2.mp3")]
;      (is (nil? (library/artist track)))))
;
;  (testing "returns the artist for a path with an artist"
;    (let [track (io/file library/*music-library* "user/artist/album/jukebox2.mp3")]
;      (is (= "artist" (library/artist track))))))
;
;(deftest finds-most-popular-artists
;  (testing "returns an array with the artist and number of tracks in descending order"
;    (is (= [["artist" 3] ["artist2" 1]] (library/most-popular-artists)))))
