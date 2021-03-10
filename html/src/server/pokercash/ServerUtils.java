package server.pokercash;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

public class ServerUtils {
    public static void printRequest(HttpServletRequest httpRequest) {
        System.out.println(" \n\n Headers");

        Enumeration headerNames = httpRequest.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            System.out.println(headerName + " = " + httpRequest.getHeader(headerName));
        }

        System.out.println("\n\nParameters");

        Enumeration params = httpRequest.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            System.out.println(paramName + " = " + httpRequest.getParameter(paramName));
        }

        System.out.println("\n\n Row data");
        System.out.println(extractPostRequestBody(httpRequest));
    }

    public static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }
}
