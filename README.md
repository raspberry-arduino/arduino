# arduino


; Install Dependencies (Libraries)
lein deps

; Build Client React bundle
lein cljsbuild once

; Run Webserver
lein ring server 7000

;Webserver on headless linux
lein ring server-headless 7000


; clean dependencies (sometimes needed when there are library conflicts)
lein clean

; show dependency tree, useful to resolve dependency conflicts.
lein deps :tree



MQTT Broker: (Needs to run on linux server, separately)
http://www.steves-internet-guide.com/install-mosquitto-linux/
MQTT Broker running on: debian.hoertlehner.com


MQTT Client Library:
http://clojuremqtt.info/articles/getting_started.html#hello-world-example


NICE REDUX DEBUGGER
https://github.com/Day8/re-frame-10x



