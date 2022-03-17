package uz.pdp.bot;

import com.fasterxml.jackson.databind.ObjectMapper;

import uz.pdp.services.*;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */
public interface UserInterface {


    BasketService basketService = new BasketService();
    FoodService foodService = new FoodService();
    CategoryService CATEGORY_SERVICE = new CategoryService();
    HistoryService historyService = new HistoryService();
    UserService userService = new UserService();

    String BOT_USERNAME = "B9_group_fast_food_bot";
    String BOT_TOKEN = "5051226387:AAE64LOgT6D835Yz_vcH4aWx2jtpUjEgL6k";

}
