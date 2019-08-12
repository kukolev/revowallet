package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Account;
import service.AccountService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static util.ServletUtils.extractPostRequestBody;

public class AccountServlet extends HttpServlet {

    private final AccountService accountService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AccountServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
// todo: throw error
        String id = request.getParameter("id");
        String phone = request.getParameter("phone");

        String result = null;
        if (id != null) {
            result = findById(id);
        } else if (phone != null) {
            result = findByPhone(phone);
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = extractPostRequestBody(req);
        Account account = mapper.readValue(s, Account.class);
        if (account.getId() == null) {
            Account result = accountService.persist(account);
            String responsePayload = mapper.writeValueAsString(result);
            resp.getWriter().println(responsePayload);
        } else {
            // todo: throw error
        }
    }

    private String findById(String accNumber) throws JsonProcessingException {
        Account account = accountService.find(Long.valueOf(accNumber));
        return mapper.writeValueAsString(account);
    }

    private String findByPhone(String phone) throws JsonProcessingException {
        List<Account> accounts = accountService.findAccountsByUserPhone(phone);
        return mapper.writeValueAsString(accounts);
    }
}
