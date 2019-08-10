package util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Scanner;

public class ServletUtils {

    public static String extractPostRequestBody(HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}