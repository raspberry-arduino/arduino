(ns arduino.views
  (:require
   [reagent.core :refer [atom]]
   [re-frame.core :as re-frame]
   [arduino.subs :as subs]
   ))

(defn hello-component [name]
  [:p "Hello, " name "!"])


(defn timer-component []
  (let [seconds-elapsed (atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div
       "Seconds Elapsed: " @seconds-elapsed])))


(defn request-data-button
  []
  [:div {:class "button-class"
         :on-click  #(re-frame.core/dispatch [:request-data])}  ;; get data from the server !!
   "I want it, now!"])


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [hello-component "TB"]
     [timer-component]
     [:p "the finest marijuana grown by arduino controlled hydroponics"]
     [request-data-button]
     ]))


