(ns cyrulik.routes.api
  (:import
   (java.util Date)
   (java.text SimpleDateFormat))
  (:require
   [cyrulik.database :as db]
   [cyrulik.middleware :as middleware]
   [ring.util.response :as resp]
   [ring.middleware.json :as json]))


(defn add-metrics! [request]
  (println (request :body))
  (db/add-metric! {:date (get-in request [:body "date"])
                   :temperature (get-in request [:body "temperature"])
                   :humidity (get-in request [:body "humidity"])})
  (resp/status 201))

(defn api-routes []
  [""
   {:middleware [json/wrap-json-body {:keywords? true}
                 middleware/wrap-check-logged-api]}
   ["/metrics" {:post add-metrics!}]])
