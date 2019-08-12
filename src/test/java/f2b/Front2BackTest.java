package f2b;

import app.Configurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Account;
import domain.User;
import dto.AddAccountDto;
import dto.Transfer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Front2BackTest {

    @BeforeAll
    public static void init() throws Exception {
        Configurator configurator = new Configurator();
        configurator.start();
        initTestData();
    }

    @Test
    public void testTransferAliceToBob() throws IOException {
        // Find money of both
        Long aliceAccontId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccontId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccontId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("541.32");
        transferMoney(aliceAccontId, bobAccontId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccontId);
        assertEquals(bobFinMoney, bobStartMoney.add(money));
        assertEquals(aliceFinMoney, aliceStartMoney.subtract(money));
    }

    @Test
    public void testTransferNothing() throws IOException {
        // Find money of both
        Long aliceAccontId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccontId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccontId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("0.0");
        transferMoney(aliceAccontId, bobAccontId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccontId);
        assertEquals(bobFinMoney, bobStartMoney);
        assertEquals(aliceFinMoney, aliceStartMoney);
    }

    @Test
    public void testTransferBobToAlice() throws IOException {
        // Find money of both
        Long aliceAccontId = queryAccountFirstByPhone("7 (777) 777-77-77");
        Long bobAccontId = queryAccountFirstByPhone("7 (666) 666-66-66");
        BigDecimal aliceStartMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobStartMoney = queryMoneyByAccountId(bobAccontId);

        // Transfer money from Bob to Alice
        BigDecimal money = new BigDecimal("123.45");
        transferMoney(bobAccontId, aliceAccontId, money);

        // Check result
        BigDecimal aliceFinMoney = queryMoneyByAccountId(aliceAccontId);
        BigDecimal bobFinMoney = queryMoneyByAccountId(bobAccontId);
        assertEquals(bobFinMoney, bobStartMoney.subtract(money));
        assertEquals(aliceFinMoney, aliceStartMoney.add(money));
    }

    private Long queryAccountFirstByPhone(String phone) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String urlPhone = phone.replace(" ", "%20");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8888/test/account?phone=" + urlPhone);
        HttpResponse response = client.execute(httpGet);
        String payload = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(payload, Account[].class);
        return accounts[0].getId();
    }

    private BigDecimal queryMoneyByAccountId(Long id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8888/test/account?id=" + id);
        HttpResponse response = client.execute(httpGet);
        String payload = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(payload, Account.class);
        return account.getMoney();
    }

    private void transferMoney(Long source, Long dest, BigDecimal money) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8888/test/account/rpc/transfer");

        Transfer transfer = new Transfer();
        transfer.setSource(source);
        transfer.setDestination(dest);
        transfer.setMoney(money);

        String transferPayload = mapper.writeValueAsString(transfer);
        httpPost.setEntity(new StringEntity(transferPayload));
        client.execute(httpPost);
    }

    private static void initTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8888/test/user");

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
        aliceAccount.setActive(true);
        aliceAccount.setUserId(alice.getId());
        aliceAccount.setMoney(new BigDecimal("1000.0"));

        Account bobAccount = new Account();
        bobAccount.setAccountNumber("4081781009991000000");
        bobAccount.setActive(true);
        bobAccount.setUserId(bob.getId());
        bobAccount.setMoney(new BigDecimal("2000.0"));

        httpPost = new HttpPost("http://localhost:8888/test/account");
        alicePayload = mapper.writeValueAsString(aliceAccount);
        httpPost.setEntity(new StringEntity(alicePayload));
        response = client.execute(httpPost);
        alicePayload = EntityUtils.toString(response.getEntity());
        aliceAccount = mapper.readValue(alicePayload, Account.class);

        bobPayload = mapper.writeValueAsString(bobAccount);
        httpPost.setEntity(new StringEntity(bobPayload));
        response = client.execute(httpPost);
        bobPayload = EntityUtils.toString(response.getEntity());
        bobAccount = mapper.readValue(bobPayload, Account.class);

//        AddAccountDto aliceAddDto = new AddAccountDto();
//        aliceAddDto.setUserId(alice.getId());
//        aliceAddDto.setAccountId(aliceAccount.getId());
//
//        AddAccountDto bobAddDto = new AddAccountDto();
//        bobAddDto.setUserId(bob.getId());
//        bobAddDto.setAccountId(bobAccount.getId());
//
//        HttpPut httpPut = new HttpPut("http://localhost:8888/test/user");
//        alicePayload = mapper.writeValueAsString(aliceAddDto);
//        httpPut.setEntity(new StringEntity(alicePayload));
//        client.execute(httpPut);
//
//        bobPayload = mapper.writeValueAsString(bobAddDto);
//        httpPut.setEntity(new StringEntity(bobPayload));
//        client.execute(httpPut);
    }
}
