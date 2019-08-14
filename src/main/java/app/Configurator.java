package app;

import dao.AccountDao;
import dao.UserDao;
import org.apache.commons.dbcp2.BasicDataSource;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import servlet.TransferByIdServlet;
import servlet.TransferByNumberServlet;
import servlet.UserServlet;
import servlet.AccountServlet;

import service.UserService;
import service.AccountService;

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
        TransferByIdServlet transferByIdServlet = new TransferByIdServlet(accountService);
        TransferByNumberServlet transferByNumberServlet = new TransferByNumberServlet(accountService);

        initServer(config, accountServlet, userServlet, transferByIdServlet, transferByNumberServlet);
    }

    private BasicDataSource initConnection(AppConfig config) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(config.getConnectionString());
        dataSource.setUsername(config.getUser());
        dataSource.setPassword(config.getPass());
        dataSource.setMinIdle(config.getMinIdle());
        dataSource.setMaxIdle(config.getMaxIdle());
        dataSource.setMaxOpenPreparedStatements(config.getMaxStatements());
        return dataSource;
    }

    private void initServer(AppConfig config,
                            AccountServlet accountServlet,
                            UserServlet userServlet,
                            TransferByIdServlet transferByIdServlet,
                            TransferByNumberServlet transferByNumberServlet) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.valueOf(config.getPort()));
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(accountServlet), "/" + config.getResource() + "/account");
        servletHandler.addServletWithMapping(new ServletHolder(transferByIdServlet), "/" + config.getResource() + "/account/rpc/transfer_by_id");
        servletHandler.addServletWithMapping(new ServletHolder(transferByNumberServlet), "/" + config.getResource() + "/account/rpc/transfer_by_number");
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
