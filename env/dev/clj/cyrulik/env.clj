(ns cyrulik.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [cyrulik.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[cyrulik started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[cyrulik has shut down successfully]=-"))
   :middleware wrap-dev})
