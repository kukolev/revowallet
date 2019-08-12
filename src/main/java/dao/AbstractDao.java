package dao;

import domain.AbstractDomain;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractDao<T extends AbstractDomain> {

    private final Connection conn;

    AbstractDao(Connection conn) {
        this.conn = conn;
    }

    Connection getConn() {
        return conn;
    }

    public abstract T find(long id);

    public abstract T persist(T obj);

    public abstract void save(T obj);
}
