(ns cyrulik.middleware
  (:require
    [cyrulik.env :refer [defaults]]
    [cheshire.generate :as cheshire]
    [cognitect.transit :as transit]
    [clojure.tools.logging :as log]
    [cyrulik.layout :refer [error-page]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [ring.util.response :as resp]
    [cyrulik.middleware.formats :as formats]
    [muuntaja.middleware :refer [wrap-format wrap-params]]
    [cyrulik.config :refer [env]]
    [ring-ttl-session.core :refer [ttl-memory-store]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [ring.middleware.session :refer [wrap-session]]
    [buddy.auth :refer [authenticated? throw-unauthorized]]
    [buddy.auth.backends :as backends]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(def backend (backends/session))

(defn login-correct? [user password]
  (let [users (get env :users)]
    (= (get-in users [(str user) :password]) (str password))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(defn wrap-auth [handler]
  (-> handler
      (wrap-authentication backend)
      (wrap-authorization backend)))

(defn wrap-check-logged [handler]
  (fn [request]
    (if-not (authenticated? request)
      (resp/redirect "/login")
      (handler request))))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-session {:cookie-attrs {:http-only true}
                     :cookie-name "cyrulik-id"})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in [:session :store] (ttl-memory-store (* 60 30)))))
      wrap-internal-error))
