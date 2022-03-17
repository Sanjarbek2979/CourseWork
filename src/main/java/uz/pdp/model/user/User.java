package uz.pdp.model.user;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.Location;
import uz.pdp.bot.MyFastFoodBot;
import uz.pdp.bot.UserState;
import uz.pdp.model.BaseModel;
import uz.pdp.model.SmsSender;

import java.time.LocalDate;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

public class User extends BaseModel {
    private String PhoneNumber;
    private UserRole userRole;
    private String smsCode;
    private String chatId="";
    private UserState userState;
    private Location location;

    {
        LocalDate orderDate = LocalDate.now();
    }

    public String setSmsCode() {
        SmsSender smsSender = new SmsSender();
        String smsCode = smsSender.randNum();
        smsSender.smsSenderMethod(smsCode, MyFastFoodBot.currentUser.getPhoneNumber());
        return smsCode;

    }


}
