(ns arduino.db)

(def default-db
  {:name "re-frame"
   :data { :test1 {:valu 13.3  :desc "Tiberius Test 1" :format "%.3f"}
           :test2 {:valu 123 :desc "Tiberius Test 2" :format "%.0f" }}
   :saying "Now! Yess - NOW!!"
   :history {
      :light1  [ 1 2 3 4 5 6 7 8 9 10 9 8 7 6 5 4 3 2 1 ]
   }
   :page :charts
   }
  )
