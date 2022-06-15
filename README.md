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

    ssh -i <private key path> username@52.138.23.19
    password: <B****@ddmmyyyy>
    username: <regular/generic>
    
    
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

Live (on Azure) API URLs:
    

INDIA - State-wise Distribution (HTML View): https://lnkd.in/fFcKdGY

Detailed (JSON View): https://lnkd.in/f3xDXkF


World Live Summary (JSON View): https://lnkd.in/guYWyvz

World Live Table of Distribution (THML View): http://52.138.23.19/world-live-detailed-browser
        

----------------

New Feature: Due to large size of the DOM content fetched from the 3 websites (captured for Scraping operation) being persisted for record-keeping, this GZIP Utility of Java Util is implemented. Now the content is compressed by ~65% +.

----------------

Added exported MongoDB Collections to "data" directory:

    mongoexport --host="localhost:PORT" --db=covid-live-db --collection=audit-exceptions --out=audit-exceptions.json
    mongoexport --host="localhost:PORT" --db=covid-live-db --collection=indiarecords --out=indiarecords.json
    mongoexport --host="localhost:PORT" --db=covid-live-db --collection=userrequests --out=userrequests.json
    mongoexport --host="localhost:PORT" --db=covid-live-db --collection=webpages --out=webpages.json
    -- mongoexport --host="localhost:PORT" --db=covid-live-db --collection=worldsummary --out=worldsummary.json (removed Collection)
    mongoexport --host="localhost:PORT" --db=covid-live-db --collection=worldsummary-new --out=worldsummary-new.json
    
    split -b 90M worldsummary-new.json.gz worldsummary-new.json.gz.part- 
        - Since the file size was huge, splitting it after GZIP.
    
    
In the latest release:
    
    1. Exceptions are being logged with complete StackTrace.
    2. NumberFormat validation updated to address empty strings bug fixed.
    3. StringIndexOutOfBounds Exception bug fixed on 21-May-2020.
    4. World Numbers HTML View endpoint introduced on 22-May-2020.
    5. Altered code to fix the issue due to MOHFW portal change by Gov. of India on 01-June-2020.
    


Areas of IMPROVEMENT:
    Today (28-June-2020), after almost 10 days, I revisited the "summary" endpoint to check the services; where I noticed significant delay in the response. After almost 30 seconds, I got response with missing DB data. After logging on to the remote server I noticed that the MongoDB server was down. The server seems to have stopped on 20 June, from the Tomcat server logs. As a better solution, thinking of introducing Kafka, where the app to publish all logs and data to Kafka Topic and another Thread/s running in the background to persist them to MongoDB. Even if MongoDB crashes, the data can be recovered from Kafka. As the cloud server may be shutdown by Microsoft due to expiry of Enterprise License, I am now considering to move this approach to the next hosting platform.
    
    
    
    
    
The entire DB backup is split across local repository and another repository located at "https://github.com/niranjan-reddy/covid-live-tracker-db_backup". 

The server is decommissioned
    - COVID Server Microservices
    - MongoDB Database
    - Loggers


The Azure server box will be used for other purposes going forward.
