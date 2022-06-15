package org.bs4lndt2.covid19.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bs4lndt2.covid19.IndiaCOVID19LiveTrackerApplication;
import org.bs4lndt2.covid19.bo.IndiaBO;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DataMapperUtil {
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	public static List<IndiaBO> convertString2XML(String inputXML, IndiaBO output) {
	    List<IndiaBO> outputList = new ArrayList<IndiaBO>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;
		int totalCases = 0;
		int totalCured = 0;
		int totalDeaths = 0;
		int totalActive = 0;
		
		try {  
		    builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(inputXML));
		    Document document = builder.parse(is);
		    NodeList nodes = document.getElementsByTagName("tr");

		    for (int i = 0; i < nodes.getLength(); i++) {
		    	output = new IndiaBO();
				Element element = (Element) nodes.item(i);
				NodeList name = element.getElementsByTagName("td");
				output.setStateName(getCharacterDataFromElement((Element) name.item(1)));
				output.setTotalCases(convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(5)))));
				output.setTotalCured(convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(3)))));
				output.setTotalDeaths(convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(4)))));
				output.setTotalActive(convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(2)))));
				outputList.add(output);
				
				totalCases += convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(5))));
				totalCured += convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(3))));
				totalDeaths += convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(4))));
				totalActive += convert2Positive(fixNumberFormatErrorIfAny(getCharacterDataFromElement((Element) name.item(2))));
		    }
		} catch (Exception e) {
			DBUtils.auditException("DataMapperUtil.convertString2XML", ExceptionUtils.getStackTrace(e));//e.toString());
		    e.printStackTrace();
		}
		IndiaCOVID19LiveTrackerApplication.totalCases = totalCases;
		IndiaCOVID19LiveTrackerApplication.totalCured = totalCured;
		IndiaCOVID19LiveTrackerApplication.totalDeaths = totalDeaths;
		IndiaCOVID19LiveTrackerApplication.totalActive = totalActive;
		IndiaCOVID19LiveTrackerApplication.totalClosed = totalCases - totalActive;
		return outputList;
	}
	
//	  public static JSONObject convertXMLString2JSON(String inputXML) { 
//		  JSONObject xmlJSONObj = null;
//		  try { 
//			  xmlJSONObj = XML.toJSONObject(inputXML); 
//			  String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
//			  //System.out.println("PRETTY JSON: " + jsonPrettyPrintString);
//			  System.out.println("BASIC JSON: " + xmlJSONObj.toString()); 
//		  } catch (JSONException je) { 
//			  System.out.println(je.toString()); 
//		  } 
//		  return xmlJSONObj;
//	  }
	  
	  public static String prettyPrintJSON(String jsonInput) {
		  return new JSONObject(jsonInput).toString(PRETTY_PRINT_INDENT_FACTOR);
	  }
	  
	  public static String convertJSON2HTML() {
		  StringBuffer response = new StringBuffer(IndiaCOVID19LiveTrackerApplication.inputXML);
		  response.replace(0, response.indexOf("<tr>"), "<html>"
		  		+ "<head><title>India Live COVID019 Status</title></head><body><table border=\"1\">"
		  		+ "<tr><td><b>Sl. No.</b></td><td><b>State / UT Name</b></td><td><b>Total Active</b></td>"
		  		+ "<td><b>Total Cured / Transferred</b></td><td><b>Total Deaths</b></td><td><b>Total Confirmed Cases</b></td></tr>");
		  response.replace(response.indexOf("</body>"), response.length(), "</table>"
		  		+ "<br/><br/>"
		  		+ "<button onClick=\"window.location.reload();\" type=\"Button\" style=\" background-color:#00CCFF; height: 50px; width: 100px; float: center;\">Refresh or Reload Data</button>"
		  		+ "</body></html>");
		  return response.toString();
	  }
	  
	  public static void convert2HTMLWorld() {
		  IndiaCOVID19LiveTrackerApplication.tableDataWorld.insert(0, "<html><head><title>World Live COVID019 Status</title></head><body>");
		  IndiaCOVID19LiveTrackerApplication.tableDataWorld.append("<br/><br/><button onClick=\"window.location.reload();\" type=\"Button\" "
		  		+ "style=\" background-color:#00CCFF; height: 50px; width: 100px; float: center;\">Refresh or Reload Data</button></body></html>");
		  //return response.toString();
	  }
	 
	
	public static String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	      CharacterData cd = (CharacterData) child;
	      return cd.getData();
	    }
	    return "";
	}
	
	private static Integer fixNumberFormatErrorIfAny(String input) {
		String regex = "[^0-9]";
        input = input.replaceAll(regex, "");
		//if (StringUtils.isNumeric(input.trim()))
		regex = "[-]?[0-9]+([.]{1}[0-9]+)?";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(input.trim());
        if(input.length() == 0)
        	return 0;
        else if(matcher.find())
            return Integer.parseInt((matcher.group(0)));
        else 
            return Integer.parseInt(input.trim());
	}
	
	private static Integer convert2Positive(Integer value) {
		return Math.abs(value);
	}
}
