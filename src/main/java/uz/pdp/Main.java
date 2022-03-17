package uz.pdp;


import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.MyFastFoodBot;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new MyFastFoodBot());

        System.out.println(EmojiParser.parseToUnicode("Bot is running"));


    }
}

