package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AccountService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static util.ServletUtils.extractPostRequestBody;

public class AccountServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(AccountServlet.class.getCanonicalName());
    private final AccountService accountService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AccountServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");
            String phone = req.getParameter("phone");
            LOGGER.debug("Start doGet: idStr = {}, phone = {}", idStr, phone);

            if (idStr != null) {
                Long id = Long.valueOf(idStr);
                findById(id, resp);
            } else if (phone != null) {
                findByPhone(phone, resp);
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e);
            resp.setStatus(SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.error(e);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
        LOGGER.debug("Finish doGet: status = {}", resp.getStatus());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String payload = extractPostRequestBody(req);
            LOGGER.debug("Start doPost: payload = {}", payload);

            Account account = mapper.readValue(payload, Account.class);
            Account result = accountService.persist(account);
            String responsePayload = mapper.writeValueAsString(result);
            resp.setStatus(SC_OK);
            resp.getWriter().println(responsePayload);

        } catch (RuntimeException e) {
            LOGGER.error(e);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
        LOGGER.debug("Finish doPost: status = {}", resp.getStatus());
    }

    private void findById(Long id, HttpServletResponse resp) throws IOException {
        Account account = accountService.find(id);
        if (account == null) {
            resp.setStatus(SC_NOT_FOUND);
        } else {
            String resultPayload = mapper.writeValueAsString(account);
            resp.setStatus(SC_OK);
            resp.getWriter().println(resultPayload);
        }
    }

    private void findByPhone(String phone, HttpServletResponse resp) throws IOException {
        List<Account> accounts = accountService.findAccountsByUserPhone(phone);
        if (accounts.isEmpty()) {
            resp.setStatus(SC_NOT_FOUND);
        } else {
            String resultPayload = mapper.writeValueAsString(accounts);
            resp.setStatus(SC_OK);
            resp.getWriter().println(resultPayload);
        }
    }
}
