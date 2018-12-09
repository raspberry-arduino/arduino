(ns arduino.config)

(def debug?
  ^boolean goog.DEBUG)

; (def base-url "arduino.hoertlehner.com")

(defn get-browser-hostname []
  (let [hostname js/window.location.hostname]
    (js/console.log "hostname is: " hostname)
    hostname
    ))

; bad ass trick by esteban to not need to change the api endpoints depending on the machine it is running on!!
 (def base-url
   (let [browser-hostname (get-browser-hostname)
         code-hostname "localhost:7000"
         hostname (if (nil? browser-hostname) code-hostname (str browser-hostname ":7000") )]
     (js/console.log "react app uses hostname:" hostname)
     hostname
     ) )

(comment

  (get-hostname)

  )





