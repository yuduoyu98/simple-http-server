package utils;

import bean.HttpHeaders;
import bean.HttpRequest;
import bean.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * utility functions
 */
public class CommonUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    static {
        // set time zone
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static String currentDate() {
        return formatDate(new Date());
    }

    public static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            Logger.error("Failed to parse date: " + date);
            return null;
        }
    }

    // client hostname/IP address, access time, requested file name, response type
    public static String generateRequestLog(HttpRequest request, HttpResponse response) {
        if (request == null || response == null) return null;
        StringBuilder sb = new StringBuilder();
        String method = request.method().toString();
        String host = request.getHeaderValue(HttpHeaders.Request.HOST);
        String dateTime = response.getHeaderValue(HttpHeaders.General.DATE);
        String resource = request.getURI().getPath();
        String status = response.statusLine.status.toString();
        sb.append(method).append(" Request [").append(host).append(" ")
                .append(dateTime).append(" ")
                .append(resource).append("] ")
                .append("Response [").append(status).append("]");
        return sb.toString();
    }

    // comparison in second-level precision
    public static boolean after(Date date1, Date date2) {
        return date1.getTime() - date2.getTime() > 1000;
    }
}
