package remcv.com.github.examprep.controller;

import java.io.File;
import java.util.List;

public interface DatabaseCrud<T>
{
    public abstract boolean add(T item);
    public abstract T read(int id);
    public abstract boolean update(T item);
    public abstract boolean delete(int id);
    public abstract void loadDb(File databaseFile);
    public abstract void saveDb(File databaseFile);
    public abstract String getDatabasePath();
    public abstract List<T> getList();
}
