package util;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    public static void initMemoryDatabase(BasicDataSource dataSource) {
        Connection conn;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            Statement statement = conn.createStatement();
            statement.execute(
                    "CREATE TABLE Accounts (\n" +
                            "account_id long auto_increment,\n" +
                            "account_number varchar(255),\n" +
                            "money numeric(10,2),\n" +
                            "user_id long\n" +
                            ");");

            statement.execute(
                    "CREATE TABLE Users (\n" +
                            "user_id long auto_increment primary key,\n" +
                            "name varchar(255),\n" +
                            "phone varchar(255)\n" +
                            ");");

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }
}
