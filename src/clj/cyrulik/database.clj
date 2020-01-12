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

(def select-notes
  {:select [:id :title :text :date :author]
   :from [:cloaca]})

(def select-speluncarum
  {:select [:id :url :note :date :author :visning :visited]
   :from [:speluncae]
   :order-by [[:visning :desc]]})

(defn get-notes [] 
  (let [notes (jdbc/query db (sql/format select-notes))]
    (reverse notes)))

(defn add-note! [data]
  (jdbc/insert! db :cloaca data))

(defn delete-note! [note-id]
  (jdbc/delete! db :cloaca ["id = ?" note-id]))

(defn get-speluncarum []
  (jdbc/query db (sql/format select-speluncarum)))

(defn add-spelunca! [data]
  (jdbc/insert! db :speluncae data))

(defn mark-visited-spelunca! [spelunca-id]
  (let [query (-> (helpers/update :speluncae)
                  (helpers/sset {:visited true})
                  (helpers/where [:= :id spelunca-id])
                  sql/format)]
    (jdbc/execute! db query)))
