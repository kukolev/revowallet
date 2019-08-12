package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static util.ServletUtils.extractPostRequestBody;

public class UserServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    // todo: throw error
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String payload = extractPostRequestBody(req);
        User user = mapper.readValue(payload, User.class);
        User persistedUser = userService.persist(user);
        String resultPayload = mapper.writeValueAsString(persistedUser);
        resp.getWriter().println(resultPayload);
    }
}
