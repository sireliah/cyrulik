(ns cyrulik.routes.auth
  (:require
   [cyrulik.config :refer [env]]
   [cyrulik.layout :as layout]
   [cyrulik.middleware :as middleware]
   [ring.util.response :as resp]
   [ring.middleware.session :refer [wrap-session]]
   [ring.util.http-response :as response]))

(defn login-correct? [user password]
  (let [users (get env :users)]
    (= (get-in users [(str user) :password]) (str password))))

(defn login-page [request]
  (layout/render request "login.html"))

(defn login-action! [request]
  (let [user (get-in request [:params :user])
        password (get-in request [:params :pass])
        session (:session request)]
    (if (login-correct? user password)
      (do
        (let [updated-session (assoc session :identity (keyword user))]
          (-> (resp/redirect "/")
              (assoc :session updated-session))))

      (resp/redirect "/login"))))

(defn logout-action! [request]
  (-> (resp/redirect "/")
      (assoc :session {}))) 

(defn auth-routes []
  [""
   {:middleware [
     middleware/wrap-csrf
     middleware/wrap-auth]}
   ["/login" {:get login-page :post login-action!}]
   ["/logout" {:get logout-action!}]])
