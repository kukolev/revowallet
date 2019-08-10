package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Transfer;
import service.AccountService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static util.ServletUtils.extractPostRequestBody;

public class TransferServlet extends HttpServlet {

    private final AccountService accountService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransferServlet(AccountService accountService) {
        this.accountService = accountService;
    }
    // todo: throw error
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String payload = extractPostRequestBody(req);
        Transfer transfer = objectMapper.readValue(payload, Transfer.class);
        accountService.transfer(transfer.getSource(), transfer.getDestination(), transfer.getMoney());
    }

}
