(ns jukebox-web.core-spec
  (:use
    [speclj.core]
    [joodo.spec-helpers.controller]
    [jukebox-web.core]))

(describe "jukebox_web"

  (with-mock-rendering)
  (with-routes app-handler)

  (it "root redirects to playlist"
    (let [response (do-get "/")]
      (should-redirect-to response "/playlist")))
  )

(run-specs)
