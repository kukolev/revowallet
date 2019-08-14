package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TransferByIdDto;
import dto.TransferByNumberDto;
import exception.AccountNotFoundException;
import exception.NotEnoughMoneyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AccountService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static util.ServletUtils.extractPostRequestBody;

public class TransferByNumberServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(TransferByNumberServlet.class.getCanonicalName());
    private final AccountService accountService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransferByNumberServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {

            String payload = extractPostRequestBody(req);
            LOGGER.debug("Start doGet: payload = {}", payload);
            TransferByNumberDto transferByNumberDto = objectMapper.readValue(payload, TransferByNumberDto.class);
            accountService.transferByNumber(transferByNumberDto.getSource(), transferByNumberDto.getDestination(), transferByNumberDto.getMoney());
            resp.setStatus(SC_OK);

        } catch (NotEnoughMoneyException | AccountNotFoundException e) {
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
}
