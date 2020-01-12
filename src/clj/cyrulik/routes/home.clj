(ns cyrulik.routes.home
  (:import
    (java.util Date)
    (java.text SimpleDateFormat))
  (:require
   [cyrulik.layout :as layout]
   [cyrulik.database :as db]
   [clojure.java.io :as io]
   [cyrulik.middleware :as middleware]
   [ring.util.response :as resp]
   [ring.middleware.session :refer [wrap-session]]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (let [notes (db/get-notes)]
    (layout/render request "home.html" {:messages notes})))

(defn add-note! [request]
  (let [title (get-in request [:params :title])
        text (get-in request [:params :text])
        user (name (get-in request [:session :identity]))
        now (java.util.Date.)]
    (db/add-note! {:title title :text text :date now :author user}))
  (resp/redirect "/"))

(defn delete-note! [request]
  (let [note-id (Integer. (get-in request [:path-params :note-id]))]
    (db/delete-note! note-id))
  (resp/redirect "/"))

(defn about-page [request]
  (layout/render request "about.html"))

(defn speluncae-page [request]
  (let [speluncae (db/get-speluncarum)]
    (layout/render request "speluncae.html" {:messages speluncae})))

(defn get-visited [request]
 (let [value (get-in request [:params :visited])]
  (if (= value "on") true false)))

(defn get-visning [request]
 (let [value (get-in request [:params :visning])]
  (if (empty? value)
    nil
    (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") value))))

(defn add-spelunca! [request]
  (db/add-spelunca!
      {:url (get-in request [:params :url])
       :note (get-in request [:params :note])
       :date (java.util.Date.)
       :visning (get-visning request)
       :author (name (get-in request [:session :identity]))
       :visited (get-visited request)})
  (resp/redirect "/speluncae"))

(defn mark-visited-spelunca! [request]
  (let [spelunca-id (Integer. (get-in request [:path-params :spelunca-id]))]
    (db/mark-visited-spelunca! spelunca-id))
  (resp/redirect "/speluncae"))

(defn home-routes []
  [""
   {:middleware [
     middleware/wrap-csrf
     middleware/wrap-auth
     middleware/wrap-formats
     middleware/wrap-check-logged]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/notes" {:post add-note!}]
   ["/notes/:note-id/delete" {:post delete-note!}]
   ["/speluncae" {:get speluncae-page :post add-spelunca!}]
   ["/speluncae/:spelunca-id/visited" {:post mark-visited-spelunca!}]])
