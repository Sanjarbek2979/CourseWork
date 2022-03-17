package uz.pdp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import uz.pdp.model.Basket;
import uz.pdp.model.user.User;
import uz.pdp.repository.BaseService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class BasketService implements BaseService<Basket, List<Basket>, String> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(List<Basket> basketList) {
        try {
            File file = new File("basketList.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, basketList);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Writing error " + e.toString());
        }
    }

    @Override
    public List<Basket> read() {
        List<Basket> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(new File("basketList.json"),
                    new TypeReference<ArrayList<Basket>>() {
                    });
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return list;

    }

    @Override
    public String add(Basket basket) {
        List<Basket> basketList = read();
        basketList.add(basket);
        write(basketList);
        return "SUCCESS";
    }

    @Override
    public Basket getById(UUID id) {
        List<Basket> basketList = read();
        for (Basket basket : basketList) {
            if (basket.getUserId().equals(id)) return basket;
        }
        return null;
    }

    public List<Basket> getBasketById(UUID id) {
        List<Basket> tempBasket = new ArrayList<>();
        for (Basket basket : read()) {
            if (basket.getUserId().equals(id))
                tempBasket.add(basket);
        }
        return tempBasket;
    }
    public Boolean isExistFood(String FoodName) {
        for (Basket basket : read()) {
            if (basket.getFoodName().equals(FoodName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public List<Basket> getAll() {
        return read();
    }

    @Override
    public String check(String basket) {
        return "SUCCESS";
    }

    public BigDecimal getTotalPrice(User user) {
        List<Basket> basketList = read();
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (Basket basket : basketList) {
            if (basket.getBasketId().equals(user.getId())) {
                totalPrice = (totalPrice.add(basket.getPrice()));
            }

        }

        return totalPrice;
    }

    public String removeByName(String foodName, UUID userId) {
        List<Basket> basketList = read();
        boolean check = basketList.removeIf(basket -> basket.getUserId().equals(userId) && basket.getFoodName().equals(foodName));
        write(basketList);
        if (check) return "SUCCESS";
        else
            return "ERROR";
    }


    public String clearBasket(UUID id) {
        List<Basket> basketList = read();
        boolean check = basketList.removeIf(basket -> basket.getUserId().equals(id));
        write(basketList);
        if (check) return "SUCCESS";
        else
            return "ERROR";
    }

}
