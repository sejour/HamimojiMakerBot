package sejour.linebot.hmb.hamimoji;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sejour.linebot.hmb.hamimoji.sequence.ElementFrameSequence;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * はみ文字アセット
 */
public class HamimojiAssets {

    private final Map<Integer, ElementFrameSequence> assets = new HashMap<>();

    /**
     * はみ文字アセットのインスタンスを生成する
     * @param assetsDirectory アセットディレクトリ
     * @throws Exception
     */
    public HamimojiAssets(String assetsDirectory) throws Exception {
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

    public ElementFrameSequence get(int codePoint) {
        return assets.get(codePoint);
    }

}
