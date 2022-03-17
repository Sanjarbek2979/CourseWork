package uz.pdp.bot;

import lombok.AllArgsConstructor;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor

public enum UserState {
    START_NEW_USER,
    START_CONTACT_SHARED,
    GET_SMS_CODE,
    START_LOCATION,
    MAIN_MENU,
    CATEGORIES,
    FOODS,
    FOOD_INFO,
    ADD_TO_CART,
    FOOD_ADDED,
    MY_CART,
    CHANGE_PRODUCT,
    BLOCKED,
    ADD_NEW_CATEGORY,
    EDIT_CATEGORY,
    DELETE_CATEGORY,
    ADD_NEW_FOOD_NAME,
    ADD_NEW_FOOD_PRICE,
    ADD_NEW_FOOD_DESCRIPTION,
    EDIT_FOOD,
    DELETE_FOOD,
    HISTORY;


}