(use 'joodo.env)

(def environment {
  :joodo.core.namespace "jukebox-web.core"
  ; environment settings go here
  })

(swap! *env* merge environment)