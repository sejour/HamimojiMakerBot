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

    private String text;

    private String textCode;

    private int columnNumber;

    public Resource(String name, String text, String textCode, int columnNumber) {
        this.name = name;
        this.text = text;
        this.textCode = textCode;
        this.columnNumber = columnNumber;
    }

}
