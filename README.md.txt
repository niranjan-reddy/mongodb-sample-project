# covid-live-tracker
Tracker for Live Status from Official / Government Portals in an aggregated view

Minimum Requirement:
1. JRE 8
2. MongoDB
3. Postman REST Client (for Testing)


-- Navigate to "target" directory on Terminal/Windows Command Prompt

Use the following command to run the local server:

    java -jar IndiaCOVID19LiveStatus-0.0.1-SNAPSHOT.jar

Use the following URL on a browser (Postman Tool Recommended).

    http://localhost:8080/status
        - Server Up/Down status and Environment Highlights
    
    http://localhost:8080/india-live-report
        - JSON response formatted
    
    http://localhost:8080/india-live-report-browser
        - HTML Table response with refresh button

    http://localhost:8080/world-live-summary
        - JSON response unformatted
    
    http://localhost:8080/world-live-detailed-browser
        - HTML Table response (very detailed) with refresh button
    
    http://localhost:800/summary
        - Admin view: India Summary, World Summary, Total User Hits, Last 20 User IP, Timestamp, requested Endpoint


The code is free of any spurious operations. You may enhance the code further if you like.


-------------------

Added MongoDB Persistence:

    show dbs
    use covid-live-db
    show collections;
    -- db.getCollectionNames();
    -- db.createCollection("collection-name");
    -- db.indiarecords.insert({ timestmp: "timestamp", record: "record" })
    -- db.userrequests.insert({ timestamp: "timestamp", remoteaddress: "remote address", remotehost: "remote host" })
    -- db.indiarecords.find();
    db.indiarecords.count();


Running the JAR file in the background (If Apache2 is installed for proxying and redirection is enabled between 80 and 8080):

    nohup java -jar target/IndiaCOVID19LiveStatus-0.0.1-SNAPSHOT.jar > console.log &
    
Otherwise, run the following so that Embedded Tomcat runs on port 80 instead of 8080:
    (Make sure you run it with "SUDO")

    sudo nohup java -jar target/IndiaCOVID19LiveStatus-0.0.1-SNAPSHOT.jar  --server.port=80 > ./logs/console-10-04-2020-23.26.log &
    
    
Now you may track the log file for any changes:
    
    tail -f ./logs/console.log
    
Connect the remote server (Ubuntu Server 16.04 LTS) via SSH:

    
    password: <PASSWORD FOR THE USER ON DB>
    username: <USERNAME ON THE DB>
    
    
Apache2 Useful Commands :

    sudo systemctl status apache2
    sudo systemctl is-enabled apache2
    sudo systemctl disable apache2
    sudo systemctl stop apache2
    sudo systemctl mask apache2
    
You may remove Apache2 by:

    sudo apt remove apache2
        however, for now I have killed the process on the server and Tomcat is listening directly.
        
        
Modify MongoDB Configurations:

    Stop Server: "sudo systemctl stop mongod"
    Modify listenIp and Port to "0.0.0.0" and appropriate port: "sudo nano /etc/mongod.conf"
    Start Server: "sudo systemctl start mongod"

Modify Azure Network Security Group settings for MongoDB port to be accessed by external entities.


-----------------

Latest features added:

    -   New Endpoints (2 nos) added
    
        -   World Live Summary        
        -   Summary (India & World)

    -   Total User-hits fetched
    -   Latest 20 User-hits data fetched
    -   Web page Scraping applied to "https://www.worldometers.info/coronavirus/" to fetch the summary section.
    -   The live data being fetched automatically every 5 minutes.
    -   Live-fetched data being persisted (every 5 mins) to MongoDB. 2 Different Collections (1. India, 2. World) used for the data.
    -   Every User-hit being persisted to MongoDB Collection (Only IP Address and Timestamp).
    
        
        
---------------
