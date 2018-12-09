(ns arduino.routes

  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]
            [arduino.views.core :refer [dashboard-page] ]
            [arduino.views.help :refer [help-page] ]
            [arduino.views.settings :refer [settings-page] ]
            [arduino.views.simulator :refer [simulator-page] ]
            [arduino.views.chart :refer [chart-page] ]

            ))


(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))



; ROUTE-DB

(def app-state (reagent/atom {}))

; ROUTES URL <=> ROUTE-DB

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (swap! app-state assoc :page :home))

  (defroute "/help" []
            (swap! app-state assoc :page :help))

  (defroute "/simulator" []
            (swap! app-state assoc :page :simulator))

  (defroute "/charts" []
            (swap! app-state assoc :page :charts))


  (defroute "/settings" []
            (swap! app-state assoc :page :settings))

  (hook-browser-navigation!))


(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [dashboard-page])

(defmethod current-page :help []
  [help-page])

(defmethod current-page :settings []
  [settings-page])

(defmethod current-page :charts []
  [chart-page])

(defmethod current-page :simulator []
  [simulator-page])

(defmethod current-page :default []
  [:div ])
