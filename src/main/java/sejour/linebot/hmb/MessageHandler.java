package sejour.linebot.hmb;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import sejour.linebot.hmb.error.UserErrorException;
import sejour.linebot.hmb.service.HamimojiMakerService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Message handler
 */
@LineMessageHandler
public class MessageHandler {

    @Autowired
    private HamimojiMakerService hamimojiMakerService;

    @EventMapping
    public List<Message> handleTextMessageEvent(MessageEvent event) {
        try {
            MessageContent messageContent = event.getMessage();
            if (messageContent instanceof TextMessageContent) {
                String text = ((TextMessageContent) messageContent).getText();
                String senderId = event.getSource().getSenderId();

                // 列数の設定
                if (!text.isEmpty() && text.charAt(0) == '#') {
                    return setColumnNumber(senderId, text);
                }
                else {
                    String imageUrl = hamimojiMakerService.make(text, senderId);

                    return Arrays.asList(
                            new ImageMessage(imageUrl, imageUrl),
                            new TextMessage(imageUrl)
                    );
                }
            }

            return handleDefaultMessageEvent(event);
        }
        catch (UserErrorException e) {
            return Arrays.asList(e.getReplyTextMessage());
        }
        catch (Throwable t) {
            System.out.println("[ERROR] " + t.getMessage());
            return Arrays.asList(new TextMessage("サーバーエラあ゛"));
        }
    }

    private Random random = new Random();

    @EventMapping
    public List<Message> handleDefaultMessageEvent(Event event) {
        String url = String.format("https://api.hokutosai.tech/bot/hmb/assets/img%d.gif", 1 + random.nextInt(121));
        return Arrays.asList(
                new ImageMessage(url, url),
                new TextMessage(url),
                new TextMessage("はみ文字に対応している文字を入力してね゛")
        );
    }

    private List<Message> setColumnNumber(String senderId, String text) throws UserErrorException {
        try {
            int columnNumber = Integer.parseInt(text.substring(1));
            hamimojiMakerService.setColumnNumber(senderId, columnNumber);
            if (columnNumber < 1) {
                return Arrays.asList(new TextMessage("改行無しに設定しました。"));
            }
            return Arrays.asList(new TextMessage(String.format("1行の長さを%d文字に設定しました。", columnNumber)));
        }
        catch (NumberFormatException e) {
            throw new UserErrorException("#の後は数字を入力してね。1行の長さを変えられるよ！", e);
        }
    }

}
