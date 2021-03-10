package server.pokercash;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PaytmServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Enumeration params = req.getParameterNames();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            stringBuilder.append("\"" + paramName + "\":\"" + req.getParameter(paramName) + "\",");
        }

        stringBuilder.append("}");
        PrintWriter out = resp.getWriter();
        resp.setHeader("Content-Type","text/html");
        out.println(
                "<html>\n" +
                        "   <head>\n" +
                        "     <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
                        "     <title>Paytm Secure Online Payment Gateway</title>\n" +
                        "   </head>\n" +
                        "   <body>\n" +
                        "      <table align='center'>\n" +
                        "            <tr>\n" +
                        "            <td><STRONG>Transaction is being processed,</STRONG></td>\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "            <td><font color='blue'>Please wait ...</font></td>\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "            <td>(Please do not press 'Refresh' or 'Back' button</td>\n" +
                        "            </tr>\n" +
                        "      </table>\n" +
                        "   <script type=\"text/javascript\">\n" +
                        "      var parentWindow=window.opener;\n" +
                        "      parentWindow.handlePaytmResponse(" + stringBuilder.toString() + ");\n" +
                        "           window.close();" +
                        "   </script>\n" +
                        "   </body>\n" +
                        "</html> "
        );
    }
}
