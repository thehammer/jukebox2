(ns jukebox-web.util.reserved-names)

(defn restricted []
  #{
    "(guest)"
    "(guest"
    "guest)"
    "guest"
    "(randomizer)"
    "(randomizer"
    "randomizer)"
    "randomizer"
    "jukebox"
    "jukebox2"
  })
