package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static util.ServletUtils.extractPostRequestBody;

public class UserServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int respStatus = SC_OK;
        try {

            String payload = extractPostRequestBody(req);
            User user = mapper.readValue(payload, User.class);
            User persistedUser = userService.persist(user);
            String resultPayload = mapper.writeValueAsString(persistedUser);
            resp.getWriter().println(resultPayload);

        } catch (RuntimeException e) {
            respStatus = SC_INTERNAL_SERVER_ERROR;
            resp.getWriter().println(e.getMessage());
        }
        resp.setStatus(respStatus);
    }
}
