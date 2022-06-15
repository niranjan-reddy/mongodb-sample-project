package org.bs4lndt2.covid19;

import java.util.*;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bs4lndt2.covid19.service.ReadWebPage;
import org.bs4lndt2.covid19.util.CompressionUtil;
import org.bs4lndt2.covid19.util.DBUtils;
import org.bs4lndt2.covid19.util.DataMapperUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
public class IndiaCOVID19LiveTrackerApplication  extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(IndiaCOVID19LiveTrackerApplication.class);
	}
	public static String inputXML = null;
	public static int totalCases = 0;
	public static int totalCured = 0;
	public static int totalDeaths = 0;
	public static int totalActive = 0;
	public static int totalClosed = 0;
	public static String allStatesIndia = null;
	public static String fullDataIndia = null;
	public static String inputXMLWorld = null;
	public static int totalCasesWorld = 0;
	public static int totalCuredWorld = 0;
	public static int totalDeathsWorld = 0;
	public static int totalActiveWorld = 0;
	public static String summaryDataWorld = null;
	public static StringBuffer tableDataWorld = null;
	public static String worldmeters = null;
	public static String mohfw = null;
	public static String whoint = null;
	private static boolean firstAttempt = true;

	public static void main(String[] args) {
		SpringApplication.run(IndiaCOVID19LiveTrackerApplication.class, args);
		try {
			ReadWebPage.checkIndiaStatus();
			ReadWebPage.checkWorldStatus();
			ReadWebPage.checkWHOsite();
			Thread.sleep(2000);
		} catch (Exception e) {
			DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.PSVM : Preinitialization", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		
		//	Timer Task for fetching live data from India Government Portal and persisting to DB
		try {
			TimerTask task = new TimerTask() {
		        public void run() {
		            System.out.println("Fetching Live Data at: " + new Date());
					try {
						if (!firstAttempt)
							ReadWebPage.checkIndiaStatus();
						DBUtils.insertRecord2DBIndia(fullDataIndia, DataMapperUtil.convertJSON2HTML());
					} catch (Exception e) {
						DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.PSVM.try : THREAD - 01", ExceptionUtils.getStackTrace(e));
						e.printStackTrace();
					}
		        }
		    };
		    Timer timer = new Timer("Live Data Poller - India");
		    long delay = 1L;
		    timer.scheduleAtFixedRate(task, delay, (5 * 60 * 1000L));
		} catch (Exception e) {
			DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.TimerTask: Live Data Poller - India", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		
		//	Timer Task for fetching live data from worldometers.info Portal and persisting to DB
		try {
			TimerTask task = new TimerTask() {
		        public void run() {
		            System.out.println("Fetching Live Data at: " + new Date());
					try {
						if (!firstAttempt)
							ReadWebPage.checkWorldStatus();
						DBUtils.insertRecord2DBWorld(summaryDataWorld, CompressionUtil.compress(tableDataWorld.toString()));
					} catch (Exception e) {
						DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.PSVM.try : THREAD - 02", ExceptionUtils.getStackTrace(e));
						e.printStackTrace();
					}
		        }
		    };
		    Timer timer = new Timer("Live Data Poller - worldometers.info");
		    long delay = 1L;
		    timer.scheduleAtFixedRate(task, delay, (5 * 60 * 1000L));
		} catch (Exception e) {
			DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.TimerTask: Live Data Poller - worldometers.info", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		
		//Timer Task for persisting 3 whole web pages to DB at 5 hours interval.
		try {
			TimerTask task = new TimerTask() {
		        public void run() {
		            System.out.println("Persisting 3 whole web pages (COMPRESSED) to DB at: " + new Date());
					try {
						if (!firstAttempt)
							ReadWebPage.checkWHOsite();
						Thread.sleep(2000);
						DBUtils.insertWebPages(CompressionUtil.compress(worldmeters), CompressionUtil.compress(mohfw), CompressionUtil.compress(whoint));
					} catch (Exception e) {
						DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.PSVM.try : THREAD - 03", ExceptionUtils.getStackTrace(e));
						e.printStackTrace();
					}
		        }
		    };
		    Timer timer = new Timer("Web Pages Recorder");
		    long delay = 1L;
		    timer.scheduleAtFixedRate(task, delay, (6 * 60 * 60 * 1000L));
		    firstAttempt = false;
		} catch (Exception e) {
			DBUtils.auditException("IndiaCOVID19LiveTrackerApplication.TimerTask: Web Pages Recorder", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	}
}
