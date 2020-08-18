package ru.homyakin.obt.telegram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.obt.config.BotConfiguration;

@Component
public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String username;
    private Integer lastDate = null;
    private final List<String> answers = Arrays.asList(
        "Вы умудрились продержаться всего %s!!!! НИЧТОЖЕСТВА! ПОЕХАЛИ ЗАНОВО",
        "О БОЖЕ МОЙ! Неужели так трудно не говорить обо мне дольше чем %s!! Да поможет мне Мелитэле....",
        "Ага! Ты был пойман за руку как дешёвка спустя %s! ОБНУЛЯЕМ ИГРУ!!",
        "Я спал всего %s.... И ты думал ускользнуть от моего взора??! НЕВЕРНЫЙ!",
        "Все было спокойно на протяжении %s... НО ТЕПЕРЬ ВЕСЬ ЧАТ СГОРИТ В ОГНЕ ПОСЛЕ ТВОИХ СЛОВ!"
    );
    private final Random random;

    public Bot(
        BotConfiguration configuration
    ) {
        token = configuration.getToken();
        username = configuration.getUsername();
        random = new Random();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && !update.getMessage().isUserMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            if (message.getChatId() != -1001090193716L) return;
            if (!message.getText().toLowerCase().contains("обт")) return;
            logger.info("New obt message: {}", message);
            SendMessage send;
            if (lastDate == null) {
                send = new SendMessage()
                    .setText("Поздравляю, ты первый упомянул ОБТ после моего пробуждения......\nДА НАЧНЁТСЯ ИГРА!")
                    .setChatId(message.getChatId())
                    .setReplyToMessageId(message.getMessageId());
            } else {
                send = new SendMessage()
                    .setText(String.format(
                        answers.get(random.nextInt(answers.size())),
                        convertSecondsToString(message.getDate() - lastDate)
                    ))
                    .setChatId(message.getChatId())
                    .setReplyToMessageId(message.getMessageId());
            }
            lastDate = message.getDate();
            sendMessage(send);
        }
    }

    public Optional<Message> sendMessage(SendMessage message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
        }
        return Optional.empty();
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private String convertSecondsToString(Integer seconds) {
        long hours = seconds / 3600;
        long minutes = seconds / 60 % 60;
        long sec = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, sec);
    }
}
