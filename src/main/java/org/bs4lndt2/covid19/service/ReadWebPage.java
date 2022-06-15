package org.bs4lndt2.covid19.service;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
//import java.util.logging.Logger;
import java.util.StringTokenizer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bs4lndt2.covid19.IndiaCOVID19LiveTrackerApplication;
import org.bs4lndt2.covid19.bo.*;
import org.bs4lndt2.covid19.util.DBUtils;
import org.bs4lndt2.covid19.util.DataMapperUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


//import org.json.JSONException;
//import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadWebPage {
	
	public static <T> void checkIndiaStatus() throws Exception{
		try {
			Document doc = Jsoup.connect("https://www.mohfw.gov.in/").get();
			IndiaCOVID19LiveTrackerApplication.mohfw = doc.toString();
			//Elements newsHeadlines = doc.select("tbody tr");
			Elements newsHeadlines = doc.select("#myModal div #main-content #state-data div div div div table.table.table-striped tbody tr");
			//System.out.println("newsHeadlines: \n" + newsHeadlines.toString());
			IndiaCOVID19LiveTrackerApplication.inputXML = "<?xml version='1.0'?><body>\n" + newsHeadlines.toString().substring(0, newsHeadlines.toString().indexOf("<tr> \n" + 
					" <td></td> \n" + 
					" <td colspan=")) + "</body>";
			//System.out.println("HTML CONTENT:\t" + IndiaCOVID19LiveTrackerApplication.inputXML);
			List<IndiaBO> resultIndia = DataMapperUtil.convertString2XML(IndiaCOVID19LiveTrackerApplication.inputXML, new IndiaBO());
			resultIndia.sort(Comparator.comparing(IndiaBO::getTotalCases).reversed());
			int counter = 1;
			for(IndiaBO item : resultIndia) {
				item.setStatePriority(counter);
				counter ++;
			}
			
			ObjectMapper mapper = new ObjectMapper();
			IndiaCOVID19LiveTrackerApplication.allStatesIndia = "{\"statewise-distribution\":" + mapper.writeValueAsString(new ArrayList<IndiaBO>(resultIndia)) + "}";
			IndiaCOVID19LiveTrackerApplication.fullDataIndia = "{\"INDIA Live COVID-19 Status\":["
					+ "{"
					+ "\"timestamp\":\"" + new Date()
					+ "\", \"total-cases\":"
					+ IndiaCOVID19LiveTrackerApplication.totalCases
					+ ", \"total-cured\":"
					+ IndiaCOVID19LiveTrackerApplication.totalCured
					+ ", \"total-deaths\":"
					+ IndiaCOVID19LiveTrackerApplication.totalDeaths
					+ ", \"total-active\":"
					+ IndiaCOVID19LiveTrackerApplication.totalActive
					+ ", \"total-closed-or-migrated\":"
					+ IndiaCOVID19LiveTrackerApplication.totalClosed
					+ "}, "
					+ IndiaCOVID19LiveTrackerApplication.allStatesIndia
					+ "]}";
			
//			System.out.println("Total Cases: " + IndiaCOVID19LiveTrackerApplication.totalCases);
//			System.out.println("Total Cured: " + IndiaCOVID19LiveTrackerApplication.totalCured);
//			System.out.println("Total Deaths: " + IndiaCOVID19LiveTrackerApplication.totalDeaths);
//			System.out.println("Total Active: " + IndiaCOVID19LiveTrackerApplication.totalActive);
		} catch (Exception e) {
			DBUtils.auditException("ReadWebPage.checkIndiaStatus", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public static <T> void checkWorldStatus() throws Exception{
		try {
			Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
			IndiaCOVID19LiveTrackerApplication.worldmeters = doc.toString();
			Elements totalCases = doc.select("div div div div #maincounter-wrap div span");
			Elements activeNClosed = doc.select("div div div div div div div div div.panel_front div.number-table-main");
			Elements tableOfDistribution = doc.select("html body div div div div div div table");
			//Elements tableOfDistribution = doc.select("html body div div div div#nav-tabContent.tab-content div#nav-today.tab-pane.active div.main_table_countries_div table#main_table_countries_today.table.table-bordered.table-hover.main_table_countries.dataTable.no-footer");
			//System.out.println("WORLD TABLE OF DISTRIBUTION: \n" + tableOfDistribution.toString());
			IndiaCOVID19LiveTrackerApplication.tableDataWorld = new StringBuffer(tableOfDistribution.toString());
			DataMapperUtil.convert2HTMLWorld();
			//System.out.println(IndiaCOVID19LiveTrackerApplication.tableDataWorld.toString());
			String total = totalCases.toString();
			String activeClosed = activeNClosed.toString();
			StringTokenizer tokensTotal = new StringTokenizer(total, "\n");
			String[] cases = new String[3];
			int index = 0;
			while (tokensTotal.hasMoreTokens()) {
				String tokenString = tokensTotal.nextToken();
				cases[index] = tokenString.substring(tokenString.indexOf(">") + 1, tokenString.indexOf("</")).trim();
				index++;
			}
			
			StringTokenizer tokensActive = new StringTokenizer(activeClosed, "\n");
			String[] active = new String[2];
			index = 0;
			while (tokensActive.hasMoreTokens()) {
				String tokenString = tokensActive.nextToken();
				if (!tokenString.contains("div")) {
					active[index] = tokenString.trim();
					index++;
				}
			}
			
			IndiaCOVID19LiveTrackerApplication.summaryDataWorld = "\"WORLD COVID-19 SUMMARY\": {\"Total Cases\":"
					+ "\"" + cases[0] + "\""
					+ ", \"Deaths\": "
					+ "\"" + cases[1] + "\""
					+ ", \"Recovered\": "
					+ "\"" + cases[2] + "\""
					+ ", \"Active Cases\": "
					+ "\"" + active[0] + "\""
					+ ", \"Closed Cases\": "
					+ "\"" + active[1] + "\""
					+ ", \"timestamp\":\"" + new Date()
					+ "\"}";
		} catch (Exception e) {
			DBUtils.auditException("ReadWebPage.checkWorldStatus", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		
		
		/*		OUTPUT based on the XPATH parameters/values:
		 * 
		 * 
		 * XPATH: "div div div div div#maincounter-wrap h1"
		 * Output:
		 *  <h1>Coronavirus Cases:</h1>
			<h1>Deaths:</h1>
			<h1>Recovered:</h1>
			
		   XPATH: "div div div div #maincounter-wrap div span"
		   Output:
		    <span style="color:#aaa">2,718,139 </span>
			<span>190,635</span>
			<span>745,500</span>
			
		   XPATH: div div div div div div div div div.panel_front div.number-table-main
		   Output:
		    <div class="number-table-main">  - active cases
			 1,782,004
			</div>
			<div class="number-table-main">  - closed cases
			 936,135
			</div>
		 */
	}
	
	public static <T> void checkWHOsite() throws Exception {
		try {
			Document doc = Jsoup.connect("https://covid19.who.int/").get();
			IndiaCOVID19LiveTrackerApplication.whoint = doc.toString();
		} catch (Exception e) {
			DBUtils.auditException("ReadWebPage.checkWHOsite", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			checkWorldStatus();
			//checkIndiaStatus();
			
//			Document doc = Jsoup.connect("https://www.mohfw.gov.in/").get();
//			System.out.println("PAGE: " + doc.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
