(ns arduino.app
  (:require [mount.core :as mount :refer [defstate]]
            [clojure.tools.logging :refer [info]]
            [arduino.web :as web]
            [arduino.influx :as influx]
            )
  (:gen-class))  ;; for -main / uberjar (no need in dev)


(defn -main [& args]
  (info "hydroponics 4 u server starting..")
  (mount/start)

  ;; registering "notify" to notify browser clients :after every state start
  ;(on-up :push #(broadcast-to-clients! http-server %)      :after)

  )

(comment

  (mount/start)
  (mount/stop)

  )