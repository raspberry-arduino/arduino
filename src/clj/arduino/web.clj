(ns arduino.web
  (:require
    [schema.core :as s]

    [ring.util.response :as response]
    [ring.util.http-response :refer :all]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.reload :refer [wrap-reload]]

    [compojure.core :refer :all] ; [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [cheshire.core :refer :all]

    [arduino.mqtt :as mqtt]
   ))

;(println "setting up routes..")

;(println "interesting topics are: " (mqtt/get-topics))

; automatically subscribe to topics of interest
;(mqtt/subscribe-topics-of-interest)

;(mqtt/subscribe "test2")


(defroutes app-routes
           ; (route/resources "/" {:root "public"})
           ;; NOTE: this will deliver your index.html
           (GET "/" [] (-> (response/resource-response "index.html" {:root "public"})
                           (response/content-type "text/html")))
         ; (GET "/" [] (myviews/home-page (instruments/get-instruments)))

           (sweet/api
             (sweet/context "/api" []
                            :tags ["api"]

                            (sweet/GET "/test" []
                              :query-params []
                              (ring.util.http-response/ok [1 2 3 4 5 6 7]))

                            (sweet/GET "/topics" []
                                       :query-params []
                                       (ring.util.http-response/ok (mqtt/get-topics)))

                            (sweet/GET "/action" []
                                       :query-params [topic :- s/Str ,  {payload :- s/Str nil}]
                                       (ring.util.http-response/ok (mqtt/do-action topic payload)))

                            ))

           (route/not-found "<h1> Sorry! Page not found.</h1>"))




(def app ( let [xx (mqtt/subscribe-topics-of-interest)]
           (wrap-defaults app-routes site-defaults)) )

;(def app (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))

;; NOTE: wrap reload isn't needed when the clj sources are watched by figwheel
;; but it's very good to know about
;(def dev-app (wrap-reload (wrap-defaults #'app-routes site-defaults)))

;(println "setting up routes..done.");

