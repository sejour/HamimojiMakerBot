package sejour.linebot.hmb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * トークルームを表す
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private int id;

    private String sender;

    private int columnNumber;

    public Room(String sender, int columnNumber) {
        this.sender = sender;
        this.columnNumber = columnNumber;
    }

}
