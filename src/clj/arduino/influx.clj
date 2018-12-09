(ns arduino.influx
  (:require [mount.core :refer [defstate]]
            [clojure.tools.logging :refer [info]]
            [clj-time.core :as t]
            [capacitor.core :as influx]                     ;influx db
            [arduino.config :refer [config]]
                             ))

(defn influx-start []
  (info "influx starting (" (get-in config [:influx :host]) ") ..")
  (println "influx started.")
  (influx/make-client (:influx config)))

(defn influx-stop []
  (info "influx stopping (but this is not supported by capacitor lib :-("))

(defstate influx-conn
          :start (influx-start)
          :stop (influx-stop))

(defn history [name]
  (let [data  (influx/db-query influx-conn (str "SELECT * FROM mqtt_consumer WHERE (time > now() - 24h) AND (\"topic\" = '" name "') ") true)
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
  ; influx-conn is created by mount/start

  (type influx-conn)
  (println influx-conn)
  (influx/ping influx-conn)
  (influx/version influx-conn)
  (influx/list-dbs influx-conn)
  (influx/db-query influx-conn "SHOW SERIES" true)
  (influx/db-query influx-conn "SELECT * FROM mqtt_consumer WHERE time > now() - 24h" true)
  (influx/db-query influx-conn "SELECT MAX(value) FROM mqtt_consumer" true)


  (influx/db-query influx-conn "SELECT used_percent FROM mem WHERE time > now() - 5h" true)
  (influx/db-query influx-conn "SELECT * FROM mqtt_consumer WHERE time > now() - 24h" true)



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