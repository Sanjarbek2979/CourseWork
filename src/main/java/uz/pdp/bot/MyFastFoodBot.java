package uz.pdp.bot;

import lombok.SneakyThrows;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.service.BotService;
import uz.pdp.model.Basket;
import uz.pdp.model.Category;
import uz.pdp.model.Food;
import uz.pdp.model.user.User;
import uz.pdp.services.CategoryService;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static uz.pdp.bot.UserState.*;
import static uz.pdp.bot.service.BotService.sendSmsCode;
import static uz.pdp.model.user.UserRole.ADMIN;
import static uz.pdp.model.user.UserRole.USER;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class MyFastFoodBot extends TelegramLongPollingBot implements UserInterface {
    public HashMap<Integer, String> categoryMessage = new HashMap<>();
    public HashMap<Integer, String> productMessage = new HashMap<>();
    public static User currentUser=null;

    BotService userBotService = new BotService();
    CategoryService categoryService = new CategoryService();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage())  {
            Message message = update.getMessage();
            String chatId = update.getMessage().getChatId().toString();
            currentUser = userService.loginByChatId(chatId);

            SendMessage sendMessage = new SendMessage();


            if(currentUser!=null){
                // This is for admin
                if(currentUser.getUserRole().equals(ADMIN)) {
                    String command = update.getMessage().getText();
                    switch (command) {
                        case "Get_All_Order_Excel_file" -> {
                            historyService.getAllOrderExcelFile();
                            InputFile inputFile = new InputFile(new File("orders.docx"));
                            SendDocument sendDocument = new SendDocument(chatId,inputFile);
                            execute(sendDocument);

                        }
                        case "Back_Main_Menu" -> {
                            sendMessage.setText("Main menu");
                            sendMessage.setChatId(chatId);
                            sendMessage.setReplyMarkup(getMainMenuKeyboard());
                            execute(sendMessage);
                        }
                        case "CRUD_Category" -> {
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("CRUD_Category");
                            sendMessage.setReplyMarkup(getCRUDCategory());
                            execute(sendMessage);
                        }
                        case "Create_Category" -> {
                            currentUser.setUserState(ADD_NEW_CATEGORY);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter new Category name: ");
                            execute(sendMessage);
                            userService.editByChatId(chatId, currentUser);
                        }
                        case "Edit_Category" -> {
                            currentUser.setUserState(EDIT_CATEGORY);
                            String res = BotService.getCategoryList() + "\n Enter name (oldName/NewName):";
                            sendMessage.setChatId(chatId);
                            sendMessage.setText(res);
                            execute(sendMessage);
                            System.out.println(userService.editByChatId(chatId, currentUser));
                        }
                        case "Get_Category_List" -> {
                            sendMessage.setText(BotService.getCategoryList());
                            sendMessage.setChatId(chatId);
                            execute(sendMessage);
                        }
                        case "Delete_Category" -> {
                            currentUser.setUserState(UserState.DELETE_CATEGORY);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter Category name: ");
                            execute(sendMessage);
                            userService.editByChatId(chatId, currentUser);
                        }
                        case "CRUD_Food" -> {
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("CRUD_Food");
                            sendMessage.setReplyMarkup(getCRUDFood());
                            execute(sendMessage);
                        }
                        case "Create_Food" -> {
                            currentUser.setUserState(ADD_NEW_FOOD_NAME);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter new Food name: ");
                            execute(sendMessage);
                            userService.editByChatId(chatId, currentUser);
                        }
                        case "Edit_Food" -> {
                            currentUser.setUserState(UserState.EDIT_FOOD);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter Food name: ");
                            execute(sendMessage);
                            userService.editByChatId(chatId, currentUser);
                        }
                        case "Get_Food_List" -> {
                            sendMessage.setChatId(chatId);
                            String response = "";
                            List<Food> list = foodService.read();
                            for (Food food : list) {
                                if (food.isActiveFood()) {
                                    response += "Category name: " + CATEGORY_SERVICE.getNameById(food.getCategoryId()) + "\nFood name: " + food.getName() + "\nFood price " + food.getPrice() + "\n\n";
                                }
                            }
                            sendMessage.setText(response);
                            execute(sendMessage);
                        }
                        case "Delete_Food" -> {
                            currentUser.setUserState(UserState.DELETE_FOOD);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter Food name: ");
                            execute(sendMessage);
                            userService.editByChatId(chatId, currentUser);
                        }
                        case "back" -> {
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Welcome Admin");
                            sendMessage.setReplyMarkup(getMainMenuKeyboard());
                            execute(sendMessage);
                        }
                        default -> {
                            switch (currentUser.getUserState()) {
                                case ADD_NEW_CATEGORY -> {
                                    Category category = new Category();
                                    category.setCategoryName(command);
                                    String res = CATEGORY_SERVICE.add(category);
                                    sendMessage.setChatId(chatId);
                                    sendMessage.setText(res);
                                    execute(sendMessage);
                                }
                                case EDIT_CATEGORY -> {
                                    String[] Name = command.split("/");
                                    sendMessage.setText(CATEGORY_SERVICE.editByName(Name[0], Name[1]));
                                    sendMessage.setChatId(chatId);
                                    currentUser.setUserState(MAIN_MENU);
                                    userService.editByChatId(chatId,currentUser);
                                    execute(sendMessage);
                                }
                                case DELETE_CATEGORY -> {
                                    sendMessage.setText(CATEGORY_SERVICE.deleteByName(command));
                                    sendMessage.setChatId(chatId);
                                    execute(sendMessage);
                                }
                                case EDIT_FOOD -> {

                                }
                                case DELETE_FOOD -> {
                                    sendMessage.setText(foodService.deleteByName(command));
                                    sendMessage.setChatId(chatId);
                                    execute(sendMessage);
                                }
                                case ADD_NEW_FOOD_NAME -> {
                                    Food newFood = new Food();
                                    newFood.setName(message.getText());
                                    foodService.add(newFood);
                                    currentUser.setUserState(ADD_NEW_FOOD_PRICE);
                                    userService.editByChatId(chatId,currentUser);
                                    sendMessage.setChatId(chatId);
                                    sendMessage.setText("Enter Food Price: ");
                                    execute(sendMessage);
                                }
                                case ADD_NEW_FOOD_PRICE -> {
                                    Food newFood = foodService.getIncompleteFood();
                                    BigDecimal tempPrice = BigDecimal.valueOf(Long.parseLong(message.getText()));
                                    newFood.setPrice(tempPrice);
                                    foodService.editFood(newFood);
                                    currentUser.setUserState(ADD_NEW_FOOD_DESCRIPTION);
                                    userService.editByChatId(chatId,currentUser);
                                    sendMessage.setText(tempPrice+"Enter Food Description: ");
                                    sendMessage.setChatId(chatId);
                                    execute(sendMessage);
                                }
                                case ADD_NEW_FOOD_DESCRIPTION -> {
                                    Food newFood = foodService.getIncompleteFood();
                                    newFood.setDescription(message.getText());
                                    foodService.editFood(newFood);
                                    sendMessage.setText("Choose food category: ");
                                    sendMessage.setChatId(chatId);
                                    sendMessage.setReplyMarkup(getCategoryInlineKeyboard());
                                    execute(sendMessage);
                                }
                                default -> {
                                    currentUser.setUserState(MAIN_MENU);
                                    userService.editByChatId(chatId,currentUser);
                                    sendMessage.setChatId(chatId);
                                    sendMessage.setText("Welcome Admin");
                                    sendMessage.setReplyMarkup(getMainMenuKeyboard());
                                    execute(sendMessage);
                                }
                            }
                        }
                    }
                }
                // This is for users
                else {
                    if (message.hasContact()) {
                        if(currentUser.getUserState().equals(START_CONTACT_SHARED)) {
                            currentUser.setUserState(GET_SMS_CODE);
                            currentUser.setPhoneNumber(message.getContact().getPhoneNumber());
                            sendSmsCode(currentUser);
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Enter Sms Code: ");
                            execute(sendMessage);
                        }
                    }
                    else if((message.getText().length()==6) && (currentUser.getUserState().equals(GET_SMS_CODE))){
                        if(currentUser.getSmsCode().equals(message.getText())) {
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Authentication successful");
                            execute(sendMessage);
                            execute(userBotService.userMainMenu(chatId));
                        }
                        else {
                            sendMessage.setText("Xato kod kiritildi: \nYana bir bor urinib koring!");
                            sendMessage.setChatId(chatId);
                            execute(sendMessage);
                            return;
                        }
                    }
                    else if (message.hasText() && !message.getText().equals("/start")) {
                        String text = message.getText();

                        if (text.equals("📋 Categories")) {
                            currentUser.setUserState(CATEGORIES);
                            userService.editByChatId(chatId,currentUser);
                        }
                        else if (text.equals("\uD83D\uDED2 My Cart")) {
                            currentUser.setUserState(MY_CART);
                            userService.editByChatId(chatId, currentUser);
                        }
                        else if (text.equals("📋 History")) {
                            execute(userBotService.getHistoryById(currentUser.getId(), chatId));
                            currentUser.setUserState(UserState.MAIN_MENU);
                            userService.editByChatId(chatId, currentUser);
                        }
                        if (currentUser.getUserState().equals(CATEGORIES)) {
                            currentUser.setUserState(UserState.FOODS);
                            userService.editByChatId(chatId, currentUser);
                            try {
                                execute(userBotService.categories(chatId));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if (message.hasLocation()) {
                        currentUser.setUserState(START_LOCATION);
                    }
                    else if(currentUser.getPhoneNumber() != null) {
                        execute(userBotService.userMainMenu(chatId));
                    }
                }
            }
            else {
                User user = new User();
                user.setUserState(START_CONTACT_SHARED);
                user.setUserRole(USER);
                user.setFullName(message.getFrom().getFirstName());
                user.setChatId(chatId);
                userService.add(user);
                execute(userBotService.sharePhoneNumber(chatId));
            }
        }
        else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            SendMessage sendMessage = new SendMessage();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            User currentUser = userService.loginByChatId(chatId);

            if (currentUser.getUserRole().equals(ADMIN)) {

                if(currentUser.getUserState().equals(ADD_NEW_FOOD_DESCRIPTION)) {
                    Food food = foodService.getIncompleteFood();
                    UUID temped = UUID.fromString(callbackQuery.getData());
                    food.setCategoryId(temped);
                    foodService.editFood(food);
                    currentUser.setUserState(MAIN_MENU);
                    userService.editByChatId(chatId,currentUser);
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("SUCCESS");
                    execute(sendMessage);
                    DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
                    execute(deleteMessage);
                }


            }
            else {
                UserState userState = currentUser.getUserState();

                if (data.equals("backToCategories")) {
                    userState = CATEGORIES;

                }
                else if (data.equals("backToProductList")) {
                    userState = FOODS;
                    data = categoryMessage.get(messageId);
                }
                else if (data.equals("myCart")) {
                    userState = MY_CART;
                }
                switch (userState) {
                    case FOODS -> {
                            categoryMessage.put(messageId, data);
                            currentUser.setUserState(FOOD_INFO);
                            userService.editByChatId(currentUser.getChatId(), currentUser);
                            execute(userBotService.foods(data, messageId, chatId));


                    }
                    case CATEGORIES -> {
                        currentUser.setUserState(FOODS);
                        userService.editByChatId(chatId, currentUser);
                        execute(userBotService.editToCategories(messageId, chatId));


                    }
                    case FOOD_INFO -> {
                        productMessage.put(messageId, data);
                        currentUser.setUserState(ADD_TO_CART);
                        userService.editByChatId(chatId, currentUser);
                        execute(userBotService.getFoodInfo(data, chatId));
                    }
                    case ADD_TO_CART -> {
                        String FoodName = data.split(" ")[0];
                        int amount = Integer.parseInt(data.split(" ")[1]);
                        Food food = foodService.checkByName(FoodName);
                        Basket myCart = new Basket();
                        if (!basketService.isExistFood(food.getName())) {
                            myCart.setPrice(food.getPrice());
                            myCart.setUserId(currentUser.getId());
                            myCart.setFoodName(food.getName());
                            myCart.setAmount(amount);
                            myCart.setBasketId(food.getCategoryId());
                            basketService.add(myCart);
                        }
                        else {
                            List<Basket> tempBasket = basketService.getBasketById(currentUser.getId());
                            for (Basket basket : tempBasket) {
                                if (basket.getFoodName().equals(food.getName()))
                                    basket.setAmount(basket.getAmount() + amount);
                            }
                            basketService.write(tempBasket);
                        }

                        currentUser.setUserState(UserState.MY_CART);
                        userService.editByChatId(chatId, currentUser);
                        try {
                            execute(userBotService.foods(categoryMessage.get(messageId), messageId, chatId));
                            execute(userBotService.productAdded(chatId));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                    }
                    case MY_CART -> {
                        currentUser.setUserState(UserState.CHANGE_PRODUCT);
                        userService.editByChatId(chatId, currentUser);
                        try {
                            execute(userBotService.myCart(chatId, currentUser.getId()));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    case CHANGE_PRODUCT -> {
                        if (!data.equals("Order now")) {
                            execute(userBotService.deleteFromCart(data, messageId, chatId, currentUser.getId()));
                            execute(userBotService.myCart(chatId, currentUser.getId()));
                        }
                        if (data.equals("Order now")) {
                            try {
                                execute(userBotService.addHistory(currentUser, chatId));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            basketService.clearBasket(currentUser.getId());
                            SendMessage message = new SendMessage();
                            message.setText("Your order accepted, Please wait a minute, We will call you");
                            message.setChatId(chatId);
                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }





    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

    private void executeEdit(InlineKeyboardMarkup i, Message message,String string){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setReplyMarkup(i);
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText(string);
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }




    public InlineKeyboardMarkup getInlineKeyboard(int t,int count) {
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        for(int i = 1; i <= count; i++) {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(String.valueOf(i));
            if(i==t){
                inlineKeyboardButton1.setText("|"+i+"|");
            }
            inlineKeyboardButton1.setCallbackData(String.valueOf(i));
            buttonRow.add(inlineKeyboardButton1);
            if(i%5==0) {
                list.add(buttonRow);
                buttonRow = new ArrayList<>();
            }
        }
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getCategoryInlineKeyboard() {
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        List<Category> categoryList = categoryService.getAll();
        boolean b = false;
        Category category1 = null;
        for(Category category:categoryList){
            if(category.isActiveFoodType()) {
                inlineKeyboardButton1 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText(category.getCategoryName());
                inlineKeyboardButton1.setCallbackData(String.valueOf(category.getId()));
                buttonRow.add(inlineKeyboardButton1);
                if (b) {
                    list.add(buttonRow);
                    buttonRow = new ArrayList<>();
                }
                b = !b;
                category1 = category;
            }
        }list.add(buttonRow);

        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getInlineKeyboard(int count) {
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        for(int i = 1; i <= count; i++) {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(String.valueOf(i));
            inlineKeyboardButton1.setCallbackData(String.valueOf(i));
            buttonRow.add(inlineKeyboardButton1);
            if(i%5==0) {
                list.add(buttonRow);
                buttonRow = new ArrayList<>();
            }
        }
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardRow keyboardButtons1 = new KeyboardRow();
        KeyboardRow keyboardButtons2 = new KeyboardRow();
        keyboardButtons.add("CRUD_Category");
        keyboardButtons1.add("CRUD_Food");
        keyboardButtons2.add("Get_All_Order_Excel_file");
        keyboardRows.add(keyboardButtons);
        keyboardRows.add(keyboardButtons1);
        keyboardRows.add(keyboardButtons2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;

    }


    public ReplyKeyboardMarkup getCRUDCategory() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardRow keyboardButtons1 = new KeyboardRow();
        KeyboardRow keyboardButtons2 = new KeyboardRow();
        KeyboardRow keyboardButtons3 = new KeyboardRow();
        KeyboardRow keyboardButtons4 = new KeyboardRow();
        keyboardButtons.add("Create_Category");
        keyboardButtons1.add("Get_Category_List");
        keyboardButtons2.add("Edit_Category");
        keyboardButtons3.add("Delete_Category");
        keyboardButtons4.add("Back_Main_Menu");
        keyboardRows.add(keyboardButtons);
        keyboardRows.add(keyboardButtons1);
        keyboardRows.add(keyboardButtons2);
        keyboardRows.add(keyboardButtons3);
        keyboardRows.add(keyboardButtons4);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;

    }

    public ReplyKeyboardMarkup getCRUDFood() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardRow keyboardButtons1 = new KeyboardRow();
        KeyboardRow keyboardButtons2 = new KeyboardRow();
        KeyboardRow keyboardButtons3 = new KeyboardRow();
        KeyboardRow keyboardButtons4 = new KeyboardRow();
        keyboardButtons.add("Create_Food");
        keyboardButtons1.add("Get_Food_List");
        keyboardButtons2.add("Edit_Food");
        keyboardButtons3.add("Delete_Food");
        keyboardButtons4.add("Back_Main_Menu");
        keyboardRows.add(keyboardButtons);
        keyboardRows.add(keyboardButtons1);
        keyboardRows.add(keyboardButtons2);
        keyboardRows.add(keyboardButtons3);
        keyboardRows.add(keyboardButtons4);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;

    }


}




