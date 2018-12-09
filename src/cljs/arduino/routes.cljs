(ns arduino.routes

  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]
            [arduino.views :as views]
            [arduino.views.help :refer [help] ]

            ))


(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))






(def app-state (reagent/atom {}))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (swap! app-state assoc :page :home))

  (defroute "/help" []
            (swap! app-state assoc :page :help))

  (defroute "/settings" []
            (swap! app-state assoc :page :settings))

  (hook-browser-navigation!))


(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [views/home])

(defmethod current-page :help []
  [help])

(defmethod current-page :settings []
  [views/settings])

(defmethod current-page :default []
  [:div ])
