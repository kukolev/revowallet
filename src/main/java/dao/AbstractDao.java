package dao;

import domain.AbstractDomain;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

abstract class AbstractDao<T extends AbstractDomain> {

    private final BasicDataSource dataSource;

    AbstractDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    Connection getConn() {
        try {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract T find(long id);

    public abstract T persist(T obj);
}
