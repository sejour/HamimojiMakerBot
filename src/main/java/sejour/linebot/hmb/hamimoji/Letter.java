package sejour.linebot.hmb.hamimoji;

import lombok.Getter;
import lombok.NonNull;

import sejour.linebot.hmb.error.UserErrorException;
import sejour.linebot.hmb.hamimoji.error.UnsupportedCharacterException;
import sejour.linebot.hmb.hamimoji.sequence.ElementFrameSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字画像の行シーケンスを表すレター
 */
public class Letter {

    // 改行のコードポイント
    private final static int LN_CODE = 10;

    private List<List<ElementFrameSequence>> lines = new ArrayList<>();

    @Getter
    private ElementFrameSequence firstCharacter;

    /**
     * テキストを入力してレターを生成する
     * @param text 入力テキスト
     * @param columnNumbers 最大列数
     * @param assets アセット
     * @throws UserErrorException
     */
    public Letter(@NonNull String text, int columnNumbers, @NonNull HamimojiAssets assets) throws UserErrorException {
        if (text.trim().isEmpty()) {
            throw new UserErrorException("文字を入力してください。");
        }

        int offset = 0, column = 1;
        int charsLength = text.length();
        for (int charIndex = 0; charIndex < charsLength; charIndex = text.offsetByCodePoints(charIndex, 1)) {
            // 改行コードによる改行
            if (text.codePointAt(charIndex) == LN_CODE) {
                addLine(new String(text.substring(offset, charIndex)), assets);
                column = 1;
                offset = charIndex + 1; /* +1で改行文字を飛ばす */
            }
            // 最大列数制限による改行
            else if (columnNumbers > 0 &&  column > columnNumbers) {
                addLine(new String(text.substring(offset, charIndex)), assets);
                column = 2; /* 現在のcharIndexのcolumnが1であり、次のcolumnは2 */
                offset = charIndex;
            }
            else {
                column += 1;
            }
        }

        addLine(new String(text.substring(offset, charsLength)), assets);
    }

    /**
     * 文字列から画像列を生成し行に追加する
     * @param string 入力文字列
     * @param assets アセット
     */
    private void addLine(String string, HamimojiAssets assets) throws UnsupportedCharacterException {
        List<ElementFrameSequence> line = new ArrayList<>();

        int charsLength = string.length();
        for (int charIndex = 0; charIndex < charsLength; charIndex = string.offsetByCodePoints(charIndex, 1)) {
            int codePoint = string.codePointAt(charIndex);
            ElementFrameSequence charFrameSequence = assets.get(codePoint); /* コードポイントからElementFrameSequenceに変換 */
            if (charFrameSequence == null) throw new UnsupportedCharacterException(codePoint);
            line.add(charFrameSequence); /* 行に追加 */
            // set first character
            if (firstCharacter == null) {
                firstCharacter = charFrameSequence;
            }
        }

        lines.add(line);
    }

    /**
     * レターの最大横幅を取得する
     */
    public int width() {
        return lines.stream().max((left, right) -> Integer.compare(left.size(), right.size())).get().size();
    }

    /**
     * レターの最大縦幅を取得する
     */
    public int height() {
        return lines.size();
    }

    /**
     * レターの行数を取得する
     */
    public int lineNumbers() {
        return lines.size();
    }

    /**
     * 行を取り出す
     * @param line 行番号
     * @return 行を表す画像列
     */
    public List<ElementFrameSequence> getLine(int line) {
        return lines.get(line);
    }

}
