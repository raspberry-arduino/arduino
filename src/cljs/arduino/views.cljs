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

(defn sensor-table []
  (let [data (re-frame/subscribe [::subs/data])
        xx (println "rendering data: " @data)]
    (fn []
      (js/setInterval #(re-frame.core/dispatch [:request-data]) 1000)
      [:div
       [:ul (for [topic @data]
              [:li {:key (get topic 0) } (get topic 0)
               [:span {:style {:color "orange"}}
                [:b ":   " (:valu (get topic 1))]  ]  ] )]
       ]
      )
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



(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [hello-component "TB"]
     [timer-component]
     [sensor-table]
     [sensor-commands]
     [:p "arduino controlled hydroponics"]
     [request-data-button]
     ]))


