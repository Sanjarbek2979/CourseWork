package uz.pdp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.pdp.model.Food;
import uz.pdp.repository.BaseService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class FoodService implements BaseService<Food, List<Food>, String> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(List<Food> foodList) {
        try {
            File file = new File("foodList.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, foodList);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Writing error " + e.toString());
        }
    }

    @Override
    public List<Food> read() {
        List<Food> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(new File("foodList.json"),
                    new TypeReference<ArrayList<Food>>() {
                    });
        }catch (Exception | NoClassDefFoundError e){
            e.printStackTrace();
        }
        return  list;
    }

    @Override
    public String add(Food food) {
        List<Food> foodList = read();
        foodList.add(food);
        write(foodList);
        return "SUCCESS";
    }

    @Override
    public Food getById(UUID id) {
        List<Food> foodList = read();
        for (int i = 0; i < foodList.size(); i++) {
            if(foodList.get(i).getId().equals(id)){
                return foodList.get(i);
            }
        }
        return null;
    }

    @Override
    public List<Food> getAll() {
        return read();
    }

    @Override
    public String check(String t) {
        return null;
    }

    public Food get(String name) {
        for (Food product : read()) {
            if (product.getName().equals(name)) {
                return product;
            }
        }

        return null;
    }

    public Food checkByName(String foodName) {
        List<Food> foodList = read();
        for (Food food:foodList) {
            if(food.getName().equals(foodName))
                return food;
        }
        return null;
    }

    public List<Food> getByCategoryId(UUID id) {
        List<Food> foodList = read();
        List<Food> response = new ArrayList<>();
        for(Food food:foodList) {
            if(food.getCategoryId().equals(id)) {
                response.add(food);
            }
        }
        return response;
    }

    public String deleteByName(String name) {
        List<Food> foodList = read();
        for(Food food:foodList) {
            if(food.getName().equals(name)) {
                food.setActiveFood(false);
                write(foodList);
                return "SUCCESS";
            }
        }return "ERROR";
    }

    public void editFood(Food newFood) {
        List<Food> foodList = read();
        int index = 0;
        for (Food food : foodList) {
            if (food.getCategoryId()==null) {
                if(food.getPrice()==null) {
                    food.setPrice(newFood.getPrice());
                }else if(food.getDescription()==null) {
                    food.setDescription(newFood.getDescription());
                }else {
                    food.setCategoryId(newFood.getCategoryId());
                }
                foodList.set(index, food);
                write(foodList);
            }
            index++;
        }
    }

    public Food getIncompleteFood() {
        List<Food> foodList = read();
        for(Food food:foodList) {
            if(food.getCategoryId()==null) {
                return food;
            }
        }
        return null;
    }

}