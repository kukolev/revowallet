package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TransferDto;
import exceptions.AccountNotFoundException;
import exceptions.NotEnoughMoneyException;
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
        try {
            String payload = extractPostRequestBody(req);
            TransferDto transferDto = objectMapper.readValue(payload, TransferDto.class);
            accountService.transfer(transferDto.getSource(), transferDto.getDestination(), transferDto.getMoney());
        } catch (Exception e) {
            if (e instanceof NotEnoughMoneyException
                    || e instanceof AccountNotFoundException) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

}
