package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TransferDto;
import exception.AccountNotFoundException;
import exception.NotEnoughMoneyException;
import service.AccountService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static util.ServletUtils.extractPostRequestBody;

public class TransferServlet extends HttpServlet {

    private final AccountService accountService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransferServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {

            String payload = extractPostRequestBody(req);
            TransferDto transferDto = objectMapper.readValue(payload, TransferDto.class);
            accountService.transfer(transferDto.getSource(), transferDto.getDestination(), transferDto.getMoney());
            resp.setStatus(SC_OK);

        } catch (NotEnoughMoneyException | AccountNotFoundException e) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
    }
}
