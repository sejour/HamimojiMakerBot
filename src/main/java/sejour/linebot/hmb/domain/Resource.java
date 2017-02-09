package sejour.linebot.hmb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成済みのはみ文字リソースを表す
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    private int id;

    private String name;

    private String sender;

    private String text;

    private String textCode;

    private String url;

    public Resource(String name, String sender, String text, String textCode, String url) {
        this.name = name;
        this.sender = sender;
        this.text = text;
        this.textCode = textCode;
        this.url = url;
    }

}
