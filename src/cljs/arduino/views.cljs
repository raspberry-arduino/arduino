(ns arduino.views
  (:require
   [reagent.core :refer [atom]]
   [re-frame.core :as re-frame]
   [arduino.subs :as subs]
   [arduino.mui :as mui]
   [oz.core :as oz]
   ))

;; define a function for generating some dummy data
(defn group-data [& names]
  (apply concat
         (for [n names]
           (map-indexed (fn [i x] {:x i :y x :col n}) (take 20 (repeatedly #(rand-int 100)))))))


(def line-plot
  {:data {:values (group-data "monkey" "slipper" "broom")}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})


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




(defn on-off-icon [name data]
  [mui/typography {:variant :title
                   :style   {:color   :yellow}}
       (if (= data "1")
         [mui/icon {:on-click #(re-frame.core/dispatch [:set-data name "0"])}  "alarm_on"]
         (if (= data "0")
           [mui/icon {:on-click #(re-frame.core/dispatch [:set-data name "1"])}  "alarm_off"]) )
     ]
  )

(defn sensor-card [name sensor-value]
  [mui/grid {:item true}
   [mui/card
      [mui/card-header {:title name }]
      [mui/card-content
          [mui/typography {:variant :title
                           :style   {:color :white}}
          sensor-value]
          [on-off-icon name sensor-value]
       ]]])


(defn sensor-cards []
  (let [data (re-frame/subscribe [::subs/data])]
    (fn []
        [mui/grid {:container   true
                   :align-items :center
                   :spacing     16}

         (for [topic @data]
              [sensor-card (get topic 0) (:valu (get topic 1))])

         ;[sensor-card "mao" 1.55]
         ;[sensor-card "tb" "sir"]
         ;[sensor-card "cat" "miao"]

        ])))


(defn lineplot-convert [name history]
  (map-indexed (fn [idx itm] (assoc {} :x idx :y itm :col name)) history))

(defn line-plot-settings [name history]
  {:data {:values (lineplot-convert name history)}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})


(defn sensor-plot [name]
  (let [ xx (re-frame.core/dispatch [:request-history name])     ; request history once at first time
         history (re-frame/subscribe [::subs/history])]      ; get the data from the re-frame db
    (fn []
      ( let [plot-data (line-plot-settings name ((keyword name) @history)) ; create vega plot from array-data
             xx (println "lineplot data: " plot-data)]
        [oz.core/vega-lite plot-data])
       )))

;

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [mui/grid {:container true}
    [mui/mui-theme-provider {:theme mui/jyu-theme-dark}
      [:div
        [:h1 "Hello from " @name]
        [timer-component]
        [sensor-table]
        [sensor-commands]

       [sensor-cards]

       [mui/icon "arrow_forward_ios"]
       [mui/icon "alarm_off"]
       [mui/icon "alarm_on"]
       [mui/divider]

        [:p "arduino controlled hydroponics"]
        [request-data-button]

        [oz.core/vega-lite line-plot]
        [sensor-plot "light1"]
        [sensor-plot "light2"]
       [sensor-plot "ph"]



     ]]]))

(defn menu [name]

  [mui/paper {:square true
              :style  {:background-color mui/secondary
                       ;; :text-align       :center
                       :padding          "1em 2em 1em 2em"}}

  [mui/grid {:container   true
             :align-items :center
             :spacing     16
             :xs 12}

   [mui/grid {:item true :xs 6}
    [mui/typography {:variant :display2
                     :style   {:color :white}}
     "Hydroponics 4 U"]]

   [mui/grid {:item true :xs 6}
    [mui/typography {:variant :title
                     :style   {:color   :white}}
     name]]


   [mui/grid {:item true :xs 4}
    [mui/typography {:variant :title
                     :style   {:color   :white}}
     [:a {:href "#/"} "sensors"]]]

   [mui/grid {:item true :xs 4}
    [mui/typography {:variant :title
                     :style   {:color   :white}}
     [:a {:href "#/help"} "help"]]]

   [mui/grid {:item true :xs 4}
    [mui/typography {:variant :title
                     :style   {:color   :white}}
     [:a {:href "#/shit"} "shit"]]]



   ]
   ])




(defn shit []
  (let [saying (re-frame/subscribe [::subs/saying])
        bongo "bongo"]
    [:div
     [menu "Shit"]
     [mui/tooltip {:title @saying
                   :on-open (fn [] ( println "tooltip onOpen"))
                   :on-close (fn [] (println "tooltip onClose")) }
       [:h1 "Now get your shit done, please!"] ]]))

(defn home []
  [:div
   [menu "Sensors"]
   [main-panel]
   ])

(defn help []
  [:div
   [menu "Help"]
   [:h1 "Help"
    [:p "Arduino based Hydroponics by TB and the gang." ]
    [:p "Sensors and Regulation."]
    [:ol
     [:li "Mao loves Clojure."]
     [:li "The cat loves to sleep."]
     [:li "TB is an C++ Ninja."]
     ]
    ]
   ])



