(ns cyrulik.routes.api
  (:import
   (java.util Date)
   (java.text SimpleDateFormat))
  (:require
   [clojure.string :as str]
   [cyrulik.database :as db]
   [cyrulik.middleware :as middleware]
   [ring.util.response :as resp]
   [ring.middleware.json :as json]))


(defn add-metrics! [request]
  (println (request :body))
  (let [date (get-in request [:body "date"])
        date-parsed (str/replace date "+00:00" "")]
    (db/add-metric! {:date date-parsed
                     :temperature (get-in request [:body "temperature"])
                     :humidity (get-in request [:body "humidity"])})
    (resp/status 201)))

(defn api-routes []
  [""
   {:middleware [json/wrap-json-body {:keywords? true}
                 middleware/wrap-check-logged-api]}
   ["/metrics" {:post add-metrics!}]])
