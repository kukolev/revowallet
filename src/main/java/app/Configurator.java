package app;

import dao.AccountDao;
import dao.UserDao;
import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlet.TransferServlet;
import servlet.UserServlet;
import service.AccountService;
import servlet.AccountServlet;
import service.UserService;

import static util.DatabaseUtils.initMemoryDatabase;

public class Configurator {

    public void start() {
        AppConfigLoader configLoader = new AppConfigLoader();
        AppConfig config = configLoader.load();

        BasicDataSource conn = initConnection(config);
        if (config.isMem()) {
            initMemoryDatabase(conn);
        }

        AccountDao accountDao = new AccountDao(conn);
        UserDao userDao = new UserDao(conn);
        AccountService accountService = new AccountService(accountDao, userDao);
        UserService userService = new UserService(userDao);
        AccountServlet accountServlet = new AccountServlet(accountService);
        UserServlet userServlet = new UserServlet(userService);
        TransferServlet transferServlet = new TransferServlet(accountService);

        initServer(config, accountServlet, userServlet, transferServlet);
    }

    private BasicDataSource initConnection(AppConfig config) {
        BasicDataSource dataSource = new BasicDataSource();
        // todo: parameters for max min
        dataSource.setUrl(config.getConnectionString());
        dataSource.setUsername(config.getUser());
        dataSource.setPassword(config.getPass());
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
        return dataSource;
    }

    private void initServer(AppConfig config,
                            AccountServlet accountServlet,
                            UserServlet userServlet,
                            TransferServlet transferServlet) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.valueOf(config.getPort()));
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(accountServlet), "/" + config.getResource() + "/account");
        servletHandler.addServletWithMapping(new ServletHolder(transferServlet), "/" + config.getResource() + "/account/rpc/transfer");
        servletHandler.addServletWithMapping(new ServletHolder(userServlet), "/" + config.getResource() + "/user");

        server.setHandler(servletHandler);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
