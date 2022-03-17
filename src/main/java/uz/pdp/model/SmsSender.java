package uz.pdp.model;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Random;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class SmsSender {

    public static final String ACCOUNT_SID = "ACbcb822218414297820b4ef4abc3bda4d";
    public static final String AUTH_TOKEN = "7a397c72ce6195b4f25d88d20f548616";

    public static void smsSenderMethod(String code,String phoneNumber) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message
                .creator(new PhoneNumber("+998900046722"), // to
                        new PhoneNumber("+15206399942"), // from
                        "Your veriflication code is " + code + "\n kodni begonalarga aslo bera ko'rmang !! \n" +
                                phoneNumber)
                .create();

    }

    public String randNum(){
        Random r = new Random();
        return String.format("%04d", r.nextInt(999999));
    }
}