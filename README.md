# arduino


## Install Clojure
```
apt-get install leiningen
```
- Clojure needs JVM, the leiningen package installs it automatically

## Editors
- Atom with Proto-Repl and Ink plugin
- InteliJ with Cursive plugin
- EMacs with CIDER plugin

# REPL
   ```
lein repl
```

# Install Dependencies (Libraries)
```
   lein deps
   ; clean dependencies (sometimes needed when there are library conflicts)
   lein clean
   ; show dependency tree, useful to resolve dependency conflicts.
   lein deps :tree

```

# Build Client React bundle
```
   lein cljsbuild once
   lein cljsbuild auto
```


# Run Webserver (THIS IS DEPRECIATED AS WE NOW RUN MOUNT WITH MULTIPLE SERVICES)
```
   lein ring server 7000
   ;Webserver on headless linux
   lein ring server-headless 7000

```


## COMPILING SERVER APP to Java Package
```
   lein uberjar
   java -jar target/arduino-0.1.0-SNAPSHOT-standalone.jar
```

## REPL
```
   lein repl
   Load a file to the repl and use its functions:
     (require '[arduino.app])
     (ns arduino.app)
     (mount/start)
```

# MQTT
- MQTT Broker: (Needs to run on linux server, separately)
- http://www.steves-internet-guide.com/install-mosquitto-linux/
- MQTT Broker running on: debian.hoertlehner.com
- MQTT Client Library: http://clojuremqtt.info/articles/getting_started.html#hello-world-example


# NICE REDUX DEBUGGER
https://github.com/Day8/re-frame-10x



# EXAMPLES TO STEAL FROM:
- https://github.com/Day8/re-frame-10x/blob/master/src/day8/re_frame_10x/view/app_db.cljs

