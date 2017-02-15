package sejour.linebot.hmb.hamimoji;

import sejour.linebot.hmb.hamimoji.error.UnsupportedCharacterException;
import sejour.linebot.hmb.hamimoji.sequence.ElementFrameSequence;

import com.sun.imageio.plugins.gif.GIFImageMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * はみ文字を生成するクラス(非推奨)
 * HamimojiBuilderを使用してください。
 */
public class HamimojiMaker {

    public final static int FRAME_COUNT = 5;
    public final static int CHARACTER_FRAME_COUNT = 5;
    public final static int FACE_FRAME_COUNT = 3;
    public final static int FRAME_SIZE = 200;

    private final Map<Integer, ElementFrameSequence> assets = new HashMap<>();

    /**
     * HamimojiMakerのインスタンスを生成する
     * @param assetsDirectory アセットディレクトリ（ディレクトリ内にhamimoji.xmlが含まれている必要がある。）
     * @throws Exception
     */
    public HamimojiMaker(String assetsDirectory) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(assetsDirectory + "/hamimoji.xml");

        Element root = document.getDocumentElement();
        if (!root.getNodeName().equals("assets")) throw new Exception("Root node must be 'assets'");

        NodeList assetsNodes = root.getChildNodes();

        for (int idxAssets = 0; idxAssets < assetsNodes.getLength(); ++idxAssets) {
            Node node = assetsNodes.item(idxAssets);
            if (node.getNodeName().equals("hamimoji") && node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String imageName = element.getAttribute("img");
                if (imageName == null || imageName.trim().length() == 0) throw new Exception("The 'img' attribute is not found.");
                ElementFrameSequence elementFrameSequence = ElementFrameSequence.load(assetsDirectory + "/" + imageName);

                NodeList hamimojiNodes = element.getChildNodes();
                for (int idxHamimoji = 0; idxHamimoji < hamimojiNodes.getLength(); ++idxHamimoji) {
                    Node charNode = hamimojiNodes.item(idxHamimoji);
                    if (charNode.getNodeName().equals("char")) {
                        String c = charNode.getTextContent().trim();
                        if (c.codePointCount(0, c.length()) != 1) throw new Exception("The content of the 'char' tag must be a character.");
                        Integer codePoint = c.codePointAt(0);
                        if (assets.containsKey(codePoint)) throw new Exception("The character is duplicated. [" + c + "]");
                        assets.put(codePoint, elementFrameSequence);
                    }
                }
            }
        }
    }

    /**
     * はみ文字画像を構築する
     * @param codePoint 入力文字のコードポイント
     * @return 生成されたはみ文字画像のGIFアニメーションシーケンス
     * @throws Exception
     */
    public List<IIOImage> build(int codePoint) throws Exception {
        List<IIOImage> frames = assets.get(codePoint).stream()
                .map(charFrame -> new IIOImage(charFrame.image, null, charFrame.metadata))
                .collect(Collectors.toList());

        if (frames == null || frames.isEmpty()) throw new UnsupportedCharacterException(codePoint);

        return frames;
    }

    /**
     * はみ文字画像を構築する
     * @param text 入力文字列
     * @return 生成されたはみ文字画像のGIFアニメーションシーケンス
     * @throws Exception
     */
    public List<IIOImage> build(String text) throws Exception {
        return build(text, 0);
    }

    /**
     * はみ文字画像を構築する
     * @param text 入力文字列
     * @param columnNumbers 最大列文字数（入力文字数が最大列文字数を超える場合は改行される。）
     * @return 生成されたはみ文字画像のGIFアニメーションシーケンス
     * @throws Exception
     */
    public List<IIOImage> build(String text, int columnNumbers) throws Exception {
        if (text == null || text.trim().length() == 0) throw new Exception("Text is not must be the null, empty or white space.");
        int charsLength = text.length();
        int textLenght = text.codePointCount(0, charsLength);

        // 文字数が最大列数よりも小さい時は右側の余白を無くすために、最大列数を無視する。
        if (textLenght <= columnNumbers) {
            columnNumbers = 0;
        }

        // 文字列から画像列を取得
        List<ElementFrameSequence> string = new ArrayList<>();
        for (int idxString = 0; idxString < charsLength; idxString = text.offsetByCodePoints(idxString, 1)) {
            int codePoint = text.codePointAt(idxString);
            ElementFrameSequence charFrameSequence = assets.get(codePoint);
            if (charFrameSequence == null) throw new UnsupportedCharacterException(codePoint);
            string.add(charFrameSequence);
        }

        List<IIOImage> frames = new ArrayList<>();

        // フレームサイズを計算
        int frameWidth = (columnNumbers > 0 ? columnNumbers : textLenght) * FRAME_SIZE;
        int frameHeight = (columnNumbers > 0 ? (int) Math.ceil((double)textLenght / (double)columnNumbers) : 1) * FRAME_SIZE;

        // 結合フレームの生成
        for (int idxFrame = 0; idxFrame < FRAME_COUNT; ++idxFrame) {
            // 下地を用意
            BufferedImage theFrame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = theFrame.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, frameWidth, frameHeight);

            CharacterFrame charFrame = null;
            GIFImageMetadata meta = null;
            int x = 0, y = 0;

            // 文字列分走査して対象のフレームの文字画像を連結する
            for (int idxText = 0; idxText < textLenght; ++idxText) {
                // 現在のインデックスの文字フレームシーケンスを取得し、対象のフレーム画像を取り出す
                charFrame = string.get(idxText).getFrame(idxFrame);
                meta = charFrame.metadata;

                // 単位座標決定
                y = (columnNumbers > 0) ? (int) Math.floor(idxText / columnNumbers) : 0;
                x = idxText - (y * columnNumbers);

                // 描画
                graphics.drawImage(charFrame.image, (x * FRAME_SIZE) + meta.imageLeftPosition, (y * FRAME_SIZE) + meta.imageTopPosition, meta.imageWidth, meta.imageHeight, null);
            }

            // 1フレーム完成
            frames.add(new IIOImage(theFrame, null, generateFrameMetadata(string.get(0).getFrame(idxFrame).metadata, frameWidth, frameHeight)));
        }

        return frames;
    }

    /**
     * フレームメタデータを生成する
     * @param source 元となるメタデータ
     * @param frameWidth フレームの横幅
     * @param frameHeight フレームの縦幅
     * @return フレームメタデータ
     */
    private static IIOMetadata generateFrameMetadata(GIFImageMetadata source, int frameWidth, int frameHeight) {
        GIFImageMetadata metadata = new GIFImageMetadata();
        metadata.imageTopPosition = 0;
        metadata.imageLeftPosition = 0;
        metadata.imageWidth = frameWidth;
        metadata.imageHeight = frameHeight;

        metadata.applicationData = source.applicationData;
        metadata.applicationIDs = source.applicationIDs;
        metadata.authenticationCodes = source.authenticationCodes;
        metadata.characterCellHeight = source.characterCellHeight;
        metadata.characterCellWidth = source.characterCellWidth;
        metadata.comments = source.comments;
        metadata.delayTime = source.delayTime;
        metadata.disposalMethod = source.disposalMethod;
        metadata.hasPlainTextExtension = source.hasPlainTextExtension;
        metadata.interlaceFlag = source.interlaceFlag;
        metadata.localColorTable = source.localColorTable;
        metadata.sortFlag = source.sortFlag;
        metadata.text = source.text;
        metadata.textBackgroundColor = source.textBackgroundColor;
        metadata.textForegroundColor = source.textForegroundColor;
        metadata.textGridHeight = source.textGridHeight;
        metadata.textGridLeft = source.textGridLeft;
        metadata.textGridTop = source.textGridTop;
        metadata.textGridWidth = source.textGridWidth;
        metadata.transparentColorFlag = source.transparentColorFlag;
        metadata.transparentColorIndex = source.transparentColorIndex;
        metadata.userInputFlag = source.userInputFlag;

        return metadata;
    }

}
