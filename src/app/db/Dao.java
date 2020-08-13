package app.db;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> getAll();
    Optional<T> getById(long id);
    boolean update(T updated);
    boolean delete(long id);
    T insert(T addition);
}
