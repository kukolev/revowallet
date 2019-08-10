package dao;

import domain.AbstractDomain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractDao<T extends AbstractDomain> {

    private Map<Long, T> data = new ConcurrentHashMap<>();
    private AtomicLong counter = new AtomicLong();

    public Map<Long, T> getData() {
        return data;
    }

    public T find(long id) {
        return data.get(id);
    }

    // todo: replace with correct exceptions
    public T persist(T obj) {
        if (obj.getId() == null) {
            long newId = counter.incrementAndGet();
            obj.setId(newId);
            save(obj);
            return obj;
        } else {
            throw new IllegalArgumentException();
        }
    }

    // todo: replace with correct exceptions
    public void save(T obj) {
        if (obj.getId() != null) {
            data.put(obj.getId(), obj);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
