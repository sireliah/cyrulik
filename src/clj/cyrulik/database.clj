(ns cyrulik.database
  (:require
   [cyrulik.config :refer [env]]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as sql]
   [mount.core :refer [defstate]]
   [honeysql.helpers :as helpers]))

(defstate db :start {:subprotocol "mysql"
         :subname (get env :connection-string)
         :user (get env :db-user)
         :password (get env :db-password)})

(def sqlmap {:select [:id :title :text :date :author]
             :from [:cloaca]})

(defn get-notes [] 
  (let [notes (jdbc/query db (sql/format sqlmap))]
    (reverse notes)))

(defn add-note! [data]
  (jdbc/insert! db :cloaca data))
