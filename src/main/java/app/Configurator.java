package app;

import dao.AccountDao;
import dao.UserDao;
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

public class Configurator {

    public void start() {
        AppConfigLoader configLoader = new AppConfigLoader();
        AppConfig config = configLoader.load();

        AccountDao accountDao = new AccountDao();
        UserDao userDao = new UserDao();
        AccountService accountService = new AccountService(accountDao, userDao);
        UserService userService = new UserService(userDao);
        AccountServlet accountServlet = new AccountServlet(accountService);
        UserServlet userServlet = new UserServlet(userService);
        TransferServlet transferServlet = new TransferServlet(accountService);

        initServer(config, accountServlet, userServlet, transferServlet);
    }


    private Server initServer(AppConfig config,
                              AccountServlet accountServlet,
                              UserServlet userServlet,
                              TransferServlet transferServlet) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.valueOf(config.getPort()));
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(accountServlet),  "/" + config.getResource() + "/account");
        servletHandler.addServletWithMapping(new ServletHolder(transferServlet),  "/" + config.getResource() + "/account/rpc/transfer");
        servletHandler.addServletWithMapping(new ServletHolder(userServlet),  "/" + config.getResource() + "/user");

        server.setHandler(servletHandler);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return server;
    }
}
