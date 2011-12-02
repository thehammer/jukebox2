(use 'joodo.env)

(def environment {
  :joodo-env "development"
  })

(swap! *env* merge environment)