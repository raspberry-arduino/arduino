(ns arduino.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [arduino.events :as events]
   [arduino.views :as views]
   [arduino.config :as config]
   [arduino.handlers :as handlers]
   [arduino.routes :as routes]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))


(defn mount-routes []
  (re-frame/clear-subscription-cache!)
  (routes/app-routes)
  (reagent/render [routes/current-page]
                  (.getElementById js/document "app")))



(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  ;(mount-root)
  (mount-routes)
  (handlers/xxx))
