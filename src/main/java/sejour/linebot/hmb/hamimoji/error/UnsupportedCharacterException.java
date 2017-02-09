package sejour.linebot.hmb.hamimoji.error;

import sejour.linebot.hmb.error.UserErrorException;

/**
 * 対応していない文字が入力された際にスローされる例外
 */
public class UnsupportedCharacterException extends UserErrorException {

    public UnsupportedCharacterException(int unsupportedCodePoint) {
        super(String.format("「%s」は、はみ文字に対応してないんです\uD83D\uDE22", new String(Character.toChars(unsupportedCodePoint))));
    }

}
