package uz.pdp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.pdp.model.user.User;
import uz.pdp.model.user.UserRole;
import uz.pdp.repository.BaseService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class UserService implements BaseService<User, List<User>, String> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(List<User> userList) {
        try {
            File file = new File("userList.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, userList);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Writing error " + e.toString());
        }
    }

    @Override
    public List<User> read() {
        List<User> list = new ArrayList<>();
        try {
            File file = new File("userList.json");
            list = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
            });
            return list;
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String add(User user) {
        List<User> userList = read();
        if (userList == null)
            userList = new ArrayList<>();
        else if (user.getUserRole().equals(UserRole.ADMIN)) {
            return "SUCCESS";
        }
        userList.add(user);
        write(userList);
        return "SUCCESS";
    }

    @Override
    public User getById(UUID id) {
        List<User> userList = read();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(id)) {
                return userList.get(i);
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> userList = read();
        return userList;
    }

    @Override
    public String check(String phoneNumber) {
        return null;
    }




    public User loginByChatId(String chatId) {
        List<User> userList = read();
        if (userList.size() != 0) {
            for (User user : userList) {
                if (user.getChatId().equals(chatId))
                    return user;
            }
        }
        return null;
    }



    public String editByChatId(String userChatId, User editedUser) {
        List<User> userList = read();
        int index = 0;
        for (User user : userList) {
            if (user.getChatId().equals(userChatId)) {
                user.setUserState(editedUser.getUserState());
                user.setSmsCode(editedUser.getSmsCode());
                if (editedUser.getFullName() != null)
                    user.setFullName(editedUser.getFullName());
                if (editedUser.getUserRole() != null)
                    user.setUserRole(editedUser.getUserRole());
                if (editedUser.getPhoneNumber() != null)
                    user.setPhoneNumber(editedUser.getPhoneNumber());
                if (editedUser.getLocation() != null)
                    user.setLocation(editedUser.getLocation());
                user.setUpdatedDate(editedUser.getCreatedDate());
                userList.set(index, user);
                write(userList);
                return "SUCCESS";
            }
            index++;
        }
        return "ERROR";
    }

}