(ns arduino.views.simulator
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [arduino.subs :as subs]
            [arduino.mui :as mui]
            [oz.core :as oz]
            [goog.string :as gstring]
            [goog.string.format]
            [arduino.views.menu :refer [menu]]
         ))


(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])


(defn sensor-commands []
  (let [data (re-frame/subscribe [::subs/data])
        xx (println "rendering commands: " @data)
        val (atom "foo")
        command (atom nil)
        ]
    (fn []
      [:div
       [:select {:on-change #(reset! command (-> % .-target .-value)) }
        ; #(println (.. % -target -value))
        (for [topic @data]
          [:option {:key (get topic 0) } (get topic 0)]
          )]
       [atom-input val]
       [:input {:type "button"
                :value "Execute!"
                :on-click #(re-frame.core/dispatch [:set-data @command @val])}]
       ]
      )))


(defn request-data-button
  []
  [:div {:class "button-class"
         :on-click  #(re-frame.core/dispatch [:request-data])}  ;; get data from the server !!
   "Update Sensor Data via Server Request!"])


(defn simulator-page []
  [:div
   [menu "Simulator"]
   [:h1 "Simulate data sent from Arduino via MQTT"]
   [:p "tiberius loves to do that!" ]
   [sensor-commands]

   [mui/divider]
   [:h1 "Update Data Manually (in case auto refresh does not work)"]
   [request-data-button]
   ])