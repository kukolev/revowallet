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

    protected Connection getConn() {
        return conn;
    }

    public abstract T find(long id);

    // todo: replace with correct exceptions
    public abstract T persist(T obj);

    // todo: replace with correct exceptions
    public abstract void save(T obj);
}
