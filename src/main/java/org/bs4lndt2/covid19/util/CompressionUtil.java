package org.bs4lndt2.covid19.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;


//ZipUtil 
public class CompressionUtil {
    public static String compress(String inputString) {
        if (inputString == null || inputString.length() == 0) {
            return inputString;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        String response = null;
		try {
			gzip = new GZIPOutputStream(out);
	        gzip.write(inputString.getBytes());
	        gzip.close();
	        response = out.toString("ISO-8859-1");
		} catch (IOException e) {
			DBUtils.auditException("CompressionUtil.compress:IOException", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		} catch (Exception e) {
			DBUtils.auditException("CompressionUtil.compress", ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
        return response;
    }

    public static void main(String[] args) throws IOException {
        String string = Jsoup.connect("https://www.mohfw.gov.in/").get().toString();
        System.out.println("SIZE: before compression: " + string.length());
        System.out.println("SIZE: after compression: " + CompressionUtil.compress(string).length());
        //System.out.println(CompressionUtil.compress(string).length());
    }
}
