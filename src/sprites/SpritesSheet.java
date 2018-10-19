package sprites;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author migo
 */
public class SpritesSheet {

    public static final Atlas readSprites(String filename) {
        List<SpriteImage> images = new LinkedList<>();
        Atlas atlas = new Atlas();
        try {

            StringBuilder stringbuilder = new StringBuilder();
            try {
                FileInputStream fileInputStream = new FileInputStream(filename);
                byte[] readBuffer = new byte[2048];
                int read = 0;
                while ((read = fileInputStream.read(readBuffer)) != -1) {
                    if (read == -1) {
                        break;
                    }
                    stringbuilder.append(new String(readBuffer, 0, read));
                }

            } catch (Exception ex) {
                Logger.getLogger(SpritesSheet.class.getName()).log(Level.SEVERE, null, ex);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(stringbuilder.toString()));

            Document document = builder.parse(inputSource);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            NamedNodeMap attributes = document.getDocumentElement().getAttributes();

            for (int ai = 0; ai < attributes.getLength(); ai++) {
                if (attributes.item(ai).getNodeName().equals("imagePath")) {
                    atlas.setImage(attributes.item(ai).getNodeValue());
                }
            }

            for (int childNode = 0; childNode < nodeList.getLength(); childNode++) {
                if (nodeList.item(childNode).getNodeName().equals("#text")) {
                    continue;
                }

                SpriteImage image = new SpriteImage();
                attributes = nodeList.item(childNode).getAttributes();
                
                for (int ai = 0; ai < attributes.getLength(); ai++) {
                    if (attributes.item(ai).getNodeName().equals("name")) {
                        image.setName(attributes.item(ai).getNodeValue());
                    }
                    if (attributes.item(ai).getNodeName().equals("x")) {
                        image.setX(Integer.parseInt(attributes.item(ai).getNodeValue()));
                    }
                    if (attributes.item(ai).getNodeName().equals("y")) {
                        image.setY(Integer.parseInt(attributes.item(ai).getNodeValue()));
                    }
                    if (attributes.item(ai).getNodeName().equals("width")) {
                        image.setWidth(Integer.parseInt(attributes.item(ai).getNodeValue()));
                    }
                    if (attributes.item(ai).getNodeName().equals("height")) {
                        image.setHeight(Integer.parseInt(attributes.item(ai).getNodeValue()));
                    }
                }
                images.add(image);
            }

        } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
        }
        atlas.setSubtexture(images);
        return atlas;
    }

}
