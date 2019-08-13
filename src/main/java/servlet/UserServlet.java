package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static util.ServletUtils.extractPostRequestBody;

public class UserServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(UserServlet.class.getCanonicalName());
    private final UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {

            String payload = extractPostRequestBody(req);
            LOGGER.debug("Start doPost: payload = {}", payload);
            User user = mapper.readValue(payload, User.class);
            User persistedUser = userService.persist(user);
            String resultPayload = mapper.writeValueAsString(persistedUser);

            resp.setStatus(SC_OK);
            resp.getWriter().println(resultPayload);

        } catch (RuntimeException e) {
            LOGGER.error(e);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
        LOGGER.debug("Finish doPost: status = {}", resp.getStatus());
    }
}
