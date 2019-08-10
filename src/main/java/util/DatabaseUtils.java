package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    public static void initMemoryDatabase(Connection conn) {
        try {
            Statement statement = conn.createStatement();
            statement.execute(
                    "CREATE TABLE Accounts (\n" +
                            "account_id long,\n" +
                            "account_number varchar(255),\n" +
                            "user_id long,\n" +
                            "is_active bool,\n" +
                            ");");

            statement.execute(
                    "CREATE TABLE Users (\n" +
                            "user_id long,\n" +
                            "name varchar(255),\n" +
                            "phone varchar(255),\n" +
                            ");");

            conn.commit();
        } catch (SQLException e) {
            // todo: change message or create own exception
            throw new RuntimeException(e);
        }
    }
}
