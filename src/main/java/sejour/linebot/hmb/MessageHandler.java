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
import org.springframework.beans.factory.annotation.Value;
import sejour.linebot.hmb.hamimoji.HamimojiWriter;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Message handler
 */
@LineMessageHandler
public class MessageHandler {

    @Autowired
    private HamimojiWriter hamimojiWriter;

    @Value("${sejour.hmb.madeUrlBase}")
    private String madeUrlBase;

    @Value("${sejour.hmb.saveDirectory}")
    private String saveDirectory;

    @Value("${sejour.hmb.defaultColumnNumber}")
    private int defaultColumnNumber;

    @EventMapping
    public List<Message> handleTextMessageEvent(MessageEvent event) {
        try {
            MessageContent messageContent = event.getMessage();
            if (messageContent instanceof TextMessageContent) {
                String text = ((TextMessageContent) messageContent).getText();
                String outFileName = UUID.randomUUID().toString() + ".gif";

                try (FileOutputStream out = new FileOutputStream(saveDirectory + "/" + outFileName)) {
                    hamimojiWriter.write(text, out, defaultColumnNumber);
                }

                String imageUrl = madeUrlBase + "/" + outFileName;
                System.out.println("[SUCCESS MAKING] " + imageUrl);
                return Arrays.asList(
                        new ImageMessage(madeUrlBase, madeUrlBase),
                        new TextMessage(imageUrl)
                );
            }
        }
        catch (Throwable t) {
            System.out.println("[ERROR] " + t.getMessage());
            return Arrays.asList(new TextMessage("サーバーエラあ゛"));
        }

        return handleDefaultMessageEvent(event);
    }

    Random random = new Random();

    @EventMapping
    public List<Message> handleDefaultMessageEvent(Event event) {
        String url = String.format("https://api.hokutosai.tech/bot/hmb/assets/img%d.gif", 1 + random.nextInt(121));
        return Arrays.asList(
                new ImageMessage(url, url),
                new TextMessage(url),
                new TextMessage("はみ文字に対応している文字を入力してね゛")
        );
    }

}
