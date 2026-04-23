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

(defn parse-int [val] (Integer/parseInt val))

(defn add-temp! [request]
  ;; (println (request :body))
  (let [date (get-in request [:body "date"])
        date-parsed (str/replace date "+00:00" "")]
    (db/add-temp! {:date date-parsed
                   :temperature (get-in request [:body "temperature"])
                   :humidity (get-in request [:body "humidity"])})
    (resp/status (resp/response "") 201)))

(defn add-dust! [request]
  (let [date (get-in request [:body "date"])
        date-parsed (str/replace date "+00:00" "")]
    (db/add-dust! {:date date-parsed
                   :pm1 (parse-int (get-in request [:body "pm1"]))
                   :pm25 (parse-int (get-in request [:body "pm25"]))
                   :pm10 (parse-int (get-in request [:body "pm10"]))})
    (resp/status (resp/response "") 201)))

(defn api-routes []
  [""
   {:middleware [json/wrap-json-body {:keywords? true}
                 middleware/wrap-check-logged-api]}
   ["/temp" {:post add-temp!}]
   ["/dust" {:post add-dust!}]])
