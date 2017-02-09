package sejour.linebot.hmb.error;

import com.linecorp.bot.model.message.TextMessage;
import lombok.Getter;

/**
 * ユーザー側の操作ミスに原因のあるエラーを表す例外
 */
public class UserErrorException extends Exception {

    public final String sendingMessage;

    public UserErrorException(String sendingMessage) {
        super();
        this.sendingMessage = sendingMessage;
    }

    public UserErrorException(String sendingMessage, Throwable cause) {
        super(cause);
        this.sendingMessage = sendingMessage;
    }

    public UserErrorException(String sendingMessage, String errorMessage) {
        super(errorMessage);
        this.sendingMessage = sendingMessage;
    }

    public UserErrorException(String sendingMessage, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.sendingMessage = sendingMessage;
    }

    public TextMessage getReplyTextMessage() {
        return new TextMessage(sendingMessage);
    }

}
