(ns cyrulik.routes.home
  (:import java.util.Date)
  (:require
   [cyrulik.config :refer [env]]
   [cyrulik.layout :as layout]
   [cyrulik.database :as db]
   [clojure.java.io :as io]
   [cyrulik.middleware :as middleware]
   [ring.util.response :as resp]
   [ring.middleware.session :refer [wrap-session]]
   [ring.util.http-response :as response]
   [buddy.auth :refer [authenticated? throw-unauthorized]]))

(defn login-correct? [user password]
  (let [users (get env :users)]
    (= (get-in users [(str user) :password]) (str password))))

(defn home-page [request]
  (if-not (authenticated? request)
    (resp/redirect "/login")
    (let [notes (db/get-notes)]
      (layout/render request "home.html" 
                     {:messages notes}))))

(defn add-note! [request]
  (let [title (get-in request [:params :title])
        text (get-in request [:params :text])
        user (name (get-in request [:session :identity]))
        now (java.util.Date.)]
    (db/add-note! {:title title :text text :date now :author user}))
  (resp/redirect "/"))

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

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [
     middleware/wrap-csrf
     middleware/wrap-auth
     middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/login" {:get login-page :post login-action!}]
   ["/logout" {:get logout-action!}]
   ["/addnote" {:post add-note!}]])
