(ns arduino.config
  (:require [mount.core :as mount :refer [defstate]]
            [clojure.edn :as edn]
            [clojure.tools.logging :refer [info]]))

(defn load-config [path]
  (info "loading config from" path)
  (-> path
      slurp
      edn/read-string))


(defstate config
          :start (load-config "resources/config.edn"))


(comment

  ; test if loading config.edn does work.
  (load-config "resources/config.edn")

  (info "test")

 )