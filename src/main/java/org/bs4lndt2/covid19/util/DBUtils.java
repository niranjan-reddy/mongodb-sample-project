package org.bs4lndt2.covid19.util;


import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DBUtils {
	private static MongoClient mongoClient = new MongoClient("20.55.75.129", 27017); // HERE YOU NEED TO PROVIDE YOUR SERVER URL AND PORT DETAILS...
	private static String db = "covid-live-db"; //DB NAME BE SUPPLIED HERE...
	private static DB database = null;
	
	private static void getConnection() {
		database = mongoClient.getDB(db);
	}
	
	public static void insertRecord2DBIndia(String inputJSONString, String inputHTMLString) {
		try {
			getConnection();
			//MongoDatabase database = mongoClient.getDatabase(db);
			DBCollection collection = database.getCollection("indiarecords");
			//MongoCollection collection = databasen.getCollection("indiarecords");
			BasicDBObject document = new BasicDBObject();
			document.put("timestamp", new Date());
			document.put("record-json", inputJSONString);
			document.put("record-html", inputHTMLString);
			collection.insert(document);
		} catch(Exception e) {
			auditException("insertRecords2DBIndia", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public static void insertRecord2DBWorld(String inputJSONString, String inputHTMLStringGZIP) {
		try {
			getConnection();
			DBCollection collection = database.getCollection("worldsummary-new");
			BasicDBObject document = new BasicDBObject();
			document.put("timestamp", new Date());
			document.put("record-json", inputJSONString);
			document.put("record-html", inputHTMLStringGZIP);
			collection.insert(document);
		} catch(Exception e) {
			auditException("insertRecords2DBWorld", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public static void insertUserLog(String requested, String remoteAddress, String remoteHost) {
		try {
			getConnection();
			DBCollection collection = database.getCollection("userrequests");
			BasicDBObject document = new BasicDBObject();
			document.put("timestamp", new Date());
			document.put("reqested", requested);
			document.put("remoteaddress", remoteAddress);
			document.put("remotehost", remoteHost);
			collection.insert(document);
		} catch(Exception e) {
			auditException("insertUserLog", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public static String recordsCount(String collectionName) {
		long result = 0;
		try {
			getConnection();
			DBCollection collection = database.getCollection(collectionName);
			result = collection.count();
		} catch(Exception e) {
			auditException("recordsCount", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return Long.toString(result);
	}
	
	public static String getRecentRecords(String collectionName, int noOfRecords) {
		JSONArray jsonarray = new JSONArray();
		try {
			getConnection();
			DBCollection collection = database.getCollection(collectionName);
			DBCursor cursor = collection.find().sort(new BasicDBObject("timestamp",-1)).limit(noOfRecords);
			JSONObject jsonobj = null;
			
			while(cursor.hasNext()) {
			    BasicDBObject obj = (BasicDBObject) cursor.next();
			    jsonobj = new JSONObject();
			    jsonobj.put("Time", obj.getString("timestamp"));
			    jsonobj.put("Resource", obj.getString("reqested"));
			    jsonobj.put("IP Address", obj.getString("remotehost"));
			    jsonarray.put(jsonobj);
			  }
		} catch(Exception e) {
			auditException("getRecentRecords", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return jsonarray.toString();
	}

	public static void insertWebPages(String worldmeters, String mohfw, String whoint) {
		try {
			getConnection();
			DBCollection collection = database.getCollection("webpages");
			BasicDBObject document = new BasicDBObject();
			document.put("timestamp", new Date());
			document.put("mohfw", mohfw);
			document.put("who", whoint);
			document.put("worldmeters", worldmeters);
			collection.insert(document);
		} catch(Exception e) {
			auditException("insertWebPages", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}		
	}
	
	public static void auditException(String source, String exception) {
		try {
			getConnection();
			DBCollection collection = database.getCollection("audit-exceptions");
			BasicDBObject document = new BasicDBObject();
			document.put("timestamp", new Date());
			document.put("source", source);
			document.put("exception", exception);
			collection.insert(document);
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
}
