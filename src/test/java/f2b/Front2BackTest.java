package f2b;

import app.Configurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Account;
import domain.User;
import dto.TransferByIdDto;
import dto.TransferByNumberDto;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Front2BackTest {

    @BeforeAll
    static void init() throws Exception {
        Configurator configurator = new Configurator();
        configurator.start();
        initTestData();
    }

    @Test
    void testTransferAliceToBob() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Alice to Bob
        BigDecimal money = new BigDecimal("541.32");
        transferMoney(aliceAccountId, bobAccountId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney.add(money));
        assertEquals(aliceFinMoney, aliceStartMoney.subtract(money));
    }

    @Test
    void testTransferNothing() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Alice to Bob
        BigDecimal money = new BigDecimal("0.0");
        transferMoney(aliceAccountId, bobAccountId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney);
        assertEquals(aliceFinMoney, aliceStartMoney);
    }

    @Test
    void testTransferBobToAlice() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("123.45");
        transferMoney(bobAccountId, aliceAccountId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney.subtract(money));
        assertEquals(aliceFinMoney, aliceStartMoney.add(money));
    }

    @Test
    void testTransferByNumberBobToAlice() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("123.45");
        transferMoneyByNumber("40817810099910000002", "40817810099910000001", money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney.subtract(money));
        assertEquals(aliceFinMoney, aliceStartMoney.add(money));
    }

    @Test
    void testTransferByNumberBobToAliceWrongNumber() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("123.45");
        transferMoneyByNumber("wrong_number_1", "wrong_number_2", money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney);
        assertEquals(aliceFinMoney, aliceStartMoney);
    }

    @Test
    void testsTransferTooMuch() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = aliceStartMoney.add(new BigDecimal("1000"));
        transferMoney(aliceAccountId, bobAccountId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney);
        assertEquals(aliceFinMoney, aliceStartMoney);
    }

    @Test
    void testsTransferWithUnknownSource() throws IOException {
        // Find money of both
        Long aliceAccountId = Long.MIN_VALUE;
        Long bobAccountId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("1000");
        int resultCode = transferMoney(aliceAccountId, bobAccountId, money);
        assertEquals(400, resultCode);

        // Check result
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccountId);
        assertEquals(bobFinMoney, bobStartMoney);
    }

    @Test
    void testsTransferWithUnknownDest() throws IOException {
        // Find money of both
        Long aliceAccountId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccountId = Long.MIN_VALUE;
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccountId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("1000");
        int resultCode = transferMoney(aliceAccountId, bobAccountId, money);
        assertEquals(400, resultCode);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccountId);
        assertEquals(aliceFinMoney, aliceStartMoney);
    }

    private Long queryAccountFirstByPhone(String phone) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String urlPhone = phone.replace(" ", "%20");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8080/test/account?phone=" + urlPhone);
        HttpResponse response = client.execute(httpGet);
        String payload = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(payload, Account[].class);
        return accounts[0].getId();
    }

    private BigDecimal queryMoneyByAccountId(Long id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8080/test/account?id=" + id);
        HttpResponse response = client.execute(httpGet);
        String payload = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(payload, Account.class);
        return account.getMoney();
    }

    private int transferMoney(Long source, Long dest, BigDecimal money) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8080/test/account/rpc/transfer_by_id");

        TransferByIdDto transferByIdDto = new TransferByIdDto();
        transferByIdDto.setSource(source);
        transferByIdDto.setDestination(dest);
        transferByIdDto.setMoney(money);

        String transferPayload = mapper.writeValueAsString(transferByIdDto);
        httpPost.setEntity(new StringEntity(transferPayload));
        HttpResponse response = client.execute(httpPost);
        return response.getStatusLine().getStatusCode();
    }

    private int transferMoneyByNumber(String source, String dest, BigDecimal money) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8080/test/account/rpc/transfer_by_number");

        TransferByNumberDto transferByNumberDto = new TransferByNumberDto();
        transferByNumberDto.setSource(source);
        transferByNumberDto.setDestination(dest);
        transferByNumberDto.setMoney(money);

        String transferPayload = mapper.writeValueAsString(transferByNumberDto);
        httpPost.setEntity(new StringEntity(transferPayload));
        HttpResponse response = client.execute(httpPost);
        return response.getStatusLine().getStatusCode();
    }

    private static void initTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8080/test/user");

        User alice = new User();
        alice.setName("Alice");
        alice.setPhone("7 (777) 777-77-77");
        String alicePayload = mapper.writeValueAsString(alice);

        User bob = new User();
        bob.setName("Bob");
        bob.setPhone("7 (666) 666-66-66");
        String bobPayload = mapper.writeValueAsString(bob);

        httpPost.setEntity(new StringEntity(alicePayload));
        HttpResponse response = client.execute(httpPost);
        alicePayload = EntityUtils.toString(response.getEntity());
        alice = mapper.readValue(alicePayload, User.class);

        httpPost.setEntity(new StringEntity(bobPayload));
        response = client.execute(httpPost);
        bobPayload = EntityUtils.toString(response.getEntity());
        bob = mapper.readValue(bobPayload, User.class);

        Account aliceAccount = new Account();
        aliceAccount.setAccountNumber("40817810099910000001");
        aliceAccount.setUserId(alice.getId());
        aliceAccount.setMoney(new BigDecimal("1000.0"));

        Account bobAccount = new Account();
        bobAccount.setAccountNumber("40817810099910000002");
        bobAccount.setUserId(bob.getId());
        bobAccount.setMoney(new BigDecimal("2000.0"));

        httpPost = new HttpPost("http://localhost:8080/test/account");
        alicePayload = mapper.writeValueAsString(aliceAccount);
        httpPost.setEntity(new StringEntity(alicePayload));
        client.execute(httpPost);

        bobPayload = mapper.writeValueAsString(bobAccount);
        httpPost.setEntity(new StringEntity(bobPayload));
        client.execute(httpPost);
    }
}
