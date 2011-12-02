(use 'joodo.env)

(def environment {
  :joodo-env "production"
  })

(swap! *env* merge environment)