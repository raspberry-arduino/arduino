(ns arduino.views
  (:require
   [reagent.core :refer [atom]]
   [re-frame.core :as re-frame]
   [arduino.subs :as subs]
   [arduino.mui :as mui]
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


(defn create-panel [tr]
    ;; "Jumbotron" header
    [mui/grid {:item true :xs 12}
     [mui/paper {:square true
                 :style  {:background-color mui/secondary
                          ;; :text-align       :center
                          :padding          "1em 2em 1em 2em"}}

      [mui/grid {:container   true
                 :align-items :center
                 :spacing     16}

       [mui/grid {:item true :xs 12}
        [mui/typography {:variant :display2
                         :style   {:color :blue}}
         "Arduino 4 Hydroponics"]]

       [mui/grid {:item true}
        [mui/typography {:variant :title
                         :style   {:color   :white}}
         (tr :sport/headline)]]

       [mui/grid {:item true}
        [mui/typography {:variant :title
                         :style   {:color   :white}}
         (tr :ice/headline)]]

       [mui/grid {:item true}
        [mui/typography {:variant :title
                         :style   {:color   :white}}
         (tr :swim/headline)]]]]])

(defn tr [_]
  "bongo")


(defn sensor-card [name sensor-value]
  [mui/grid {:item true}
   [mui/card
      [mui/card-header {:title name }]
      [mui/card-content
          [mui/typography {:variant :title
                           :style   {:color :white}}
          sensor-value]]]])


(defn sensor-cards []
  (let [data (re-frame/subscribe [::subs/data])]
    (fn []
        [mui/grid {:container   true
                   :align-items :center
                   :spacing     16}

         (for [topic @data]
              [sensor-card (get topic 0) (:valu (get topic 1))])

         [sensor-card "mao" 1.55]
         [sensor-card "tb" 7.31]
   ])))






(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [mui/grid {:container true}
    [mui/mui-theme-provider {:theme mui/jyu-theme-dark}
      [create-panel tr]
      [:div
        [:h1 "Hello from " @name]
        [hello-component "TB"]
        [timer-component]
        [sensor-table]
        [sensor-commands]

       [sensor-cards]

       [mui/divider]

        [:p "arduino controlled hydroponics"]
        [request-data-button]

     ]]]))


