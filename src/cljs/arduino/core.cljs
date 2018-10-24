(ns arduino.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [arduino.events :as events]
   [arduino.views :as views]
   [arduino.config :as config]
   [arduino.handlers :as handlers]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (handlers/xxx))
