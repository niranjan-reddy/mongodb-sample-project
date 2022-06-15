package org.bs4lndt2.covid19;

//import org.json.JSONObject;

//import java.util.concurrent.atomic.AtomicLong;

//import java.util.Date;

import javax.servlet.http.HttpServletRequest;

//import java.sql.Timestamp;

import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bs4lndt2.covid19.util.DBUtils;
import org.bs4lndt2.covid19.util.DataMapperUtil;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class LiveTrackerController {
	
	private static final String template = "\"COVID19 Live Tracker Server\" is Up & Running : Microsoft Azure >> Ubuntu Server 16.04 LTS >> Apache Tomcat 9.x >> Spring Boot Application";
	
	@GetMapping("/status")
	public String status(@RequestParam(value = "name", defaultValue = "Server") String name) {
		System.out.println("Server Status-check Request Served...");
		return new String(template);
	}
	
	@GetMapping(value="/summary", produces= {"application/json"})
	public ResponseEntity<String> summary(@RequestParam(value = "name", defaultValue = "Server") String name) {
		String response = null;
		try {
			System.out.println("SUMMARY Request Served...");
			response = "{\"SUMMARY - India & World\":["
					+ "{\"INDIA CCOVID-19 SUMMARY\":"
					+ "{\"Total Cases\":"
					+ IndiaCOVID19LiveTrackerApplication.totalCases
					+ ", \"Deaths\":"
					+ IndiaCOVID19LiveTrackerApplication.totalDeaths
					+ ", \"Recovered\":"
					+ IndiaCOVID19LiveTrackerApplication.totalCured
					+ ", \"Active Cases\":"
					+ IndiaCOVID19LiveTrackerApplication.totalActive
					+ ", \"Total Closed/Migrated\":"
					+ 	IndiaCOVID19LiveTrackerApplication.totalClosed
					+ "}}, {"
					+ IndiaCOVID19LiveTrackerApplication.summaryDataWorld
					+ "}, {\"TOTAL User Hits\":"
					+ DBUtils.recordsCount("userrequests")
					+ "}, {\"Latest 20 User Hits\":"
					+ DBUtils.getRecentRecords("userrequests", 20)
					+ "}"
					+ "]}";
		} catch (Exception e) {
			DBUtils.auditException("LiveTrackerController.summary", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@GetMapping(value="/india-live-report", produces= {"application/json"})
	public ResponseEntity<String> indiaLiveReport(HttpServletRequest request) {
		JSONObject response = null;
		try {
			response = new JSONObject(IndiaCOVID19LiveTrackerApplication.fullDataIndia);
			DBUtils.insertUserLog("JSON-IN",request.getRemoteAddr(), request.getRemoteHost());
		} catch (Exception e) {
			DBUtils.auditException("LiveTrackerController.indiaLiveReport", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return new ResponseEntity<String>(DataMapperUtil.prettyPrintJSON(response.toString()), HttpStatus.OK);
	}
	
	@GetMapping(value="/world-live-summary", produces= {"application/json"})
	public ResponseEntity<String> worldLiveSummary(HttpServletRequest request) {
		try {
			DBUtils.insertUserLog("JSON-WORLD",request.getRemoteAddr(), request.getRemoteHost());
			//DataMapperUtil.prettyPrintJSON(response.toString()), HttpStatus.OK);
		} catch (Exception e) {
			DBUtils.auditException("LiveTrackerController.worldLiveSummary", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return new ResponseEntity<String>(IndiaCOVID19LiveTrackerApplication.summaryDataWorld, HttpStatus.OK);
	}
	
	@GetMapping(value="/world-live-detailed-browser", produces= {"text/html"})
	public ResponseEntity<String> worldLiveDetailedBrowser(HttpServletRequest request) {
		try {
			DBUtils.insertUserLog("HTML-WORLD",request.getRemoteAddr(), request.getRemoteHost());
		} catch (Exception e) {
			DBUtils.auditException("LiveTrackerController.worldLiveDetailedBrowser", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return new ResponseEntity<String>(IndiaCOVID19LiveTrackerApplication.tableDataWorld.toString(), HttpStatus.OK);
	}
	
	@GetMapping(value="/india-live-report-browser", produces= {"text/html"})
	public ResponseEntity<String> indiaLiveReportBrowser(HttpServletRequest request) {
		try {
			DBUtils.insertUserLog("HTML-IN",request.getRemoteAddr(), request.getRemoteHost());
		} catch (Exception e) {
			DBUtils.auditException("LiveTrackerController.indiaLiveReportBrowser", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return new ResponseEntity<String>(DataMapperUtil.convertJSON2HTML(), HttpStatus.OK);
	}
}
