package uz.pdp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.pdp.model.Category;
import uz.pdp.repository.BaseService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class CategoryService implements BaseService<Category, List<Category>,String> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(List<Category> categoryList) {
        try {
            File file = new File("foodTypeList.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, categoryList);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Writing error " + e.toString());
        }

    }

    @Override
    public List<Category> read() {
        List<Category> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(new File("foodTypeList.json"),
                    new TypeReference<ArrayList<Category>>() {
                    });
        }catch (Exception | NoClassDefFoundError e){
            e.printStackTrace();
        }
        return  list;
    }

    @Override
    public String add(Category category) {
        List<Category> categoryList = read();
        categoryList.add(category);
        write(categoryList);
        return "SUCCESS";
    }

    @Override
    public Category getById(UUID id) {
        return null;
    }

    @Override
    public List<Category> getAll() {
        return read();
    }

    @Override
    public String check(String t) {
        List<Category> categoryList = read();
        for(Category f:categoryList) {
            if(f.getCategoryName().equals(t)) {
                return "ERROR";
            }
        }
        return "SUCCESS";
    }


    public String deleteByName(String name) {
        List<Category> list = read();
        boolean b=false;
        for(Category category :list) {
            if(category.getCategoryName().equals(name)) {
                category.setActiveFoodType(false);
                b=true;
                break;
            }
        }
        if(b) {
            write(list);
            return "SUCCESS";
        }
        return "ERROR";
    }

    public String getNameById(UUID id) {
        List<Category> list = read();
        for(Category category :list) {
            if(category.getId().equals(id)) {
                return category.getCategoryName();
            }
        }
        return "";
    }

    public String editByName(String oldName, String newName) {
        List<Category> list = read();
        for(Category category :list) {
            if(category.getCategoryName().equals(oldName)) {
                category.setCategoryName(newName);
                write(list);
                return "SUCCESS";
            }
        }
        return "ERROR";
    }

}