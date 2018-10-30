(ns arduino.influx
  (:require [clj-time.core :as t]
            [capacitor.core :as influx]
                             ))


(def client ( influx/make-client {
                                  :host     "metrics.hoertlehner.com"
                                   :scheme   "http"
                                   :port     8086
                                   ; :username "root"
                                   ; :password "root"
                                   :db       "demo"
                                   :version  "0.9"}
                                 ))


(defn history [name]
  (let [data  (influx/db-query client (str "SELECT * FROM mqtt_consumer WHERE (time > now() - 24h) AND (\"topic\" = '" name "') ") true)
        results (:results data)
        series (get (:series (get results 0)) 0)
        vals ( :values series)
        ts (map #(get % 3) vals)
        ]
    ts))

(defn lineplot-convert [name history]
    (map-indexed (fn [idx itm] (assoc {} :x idx :y itm :col name)) history))

(defn line-plot-settings [name history]
  {:data {:values (lineplot-convert name history)}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})


(comment  ;*****************************************************
  (influx/ping client)
  (influx/list-dbs client)
  (influx/db-query client "SHOW SERIES" true)
  (influx/db-query client "SELECT * FROM mqtt_consumer WHERE time > now() - 24h" true)
  (influx/db-query client "SELECT MAX(value) FROM mqtt_consumer" true)


  (history "light1")
  (lineplot-convert "light2" (history "light2"))

  (defn group-data [& names]
    (apply concat
           (for [n names]
             (map-indexed (fn [i x] {:x i :y x :col n}) (take 20 (repeatedly #(rand-int 100)))))))


  (group-data "bongo" "billa")


  (line-plot-settings "bongo" [1 3 4 5 3 4 6])
  (line-plot-settings "bongo" [])



  ) ;*****************************************************