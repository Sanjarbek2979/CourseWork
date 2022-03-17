package uz.pdp.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class Util<T>{

    static ObjectMapper objectMapper = new ObjectMapper();

    public List<T> read(String s){
        List<T> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(new File("userList.json"),
                    new TypeReference<ArrayList<T>>() {
            });
        }catch (Exception | NoClassDefFoundError e){
            e.printStackTrace();
        }
        return  list;
    }

    public void write(String s,T t){
        List<T> list = read(s);

        list.add(t);
        try {
            objectMapper.writeValue(new File("userList.json"),
                    list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
