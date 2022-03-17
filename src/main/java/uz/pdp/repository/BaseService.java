package uz.pdp.repository;


import java.util.UUID;
/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public interface BaseService<T,L,R>  {


    void write(L l);
    L read();



    R add(T t);
    T getById(UUID id);
    L getAll();
    R check(R t);
}
