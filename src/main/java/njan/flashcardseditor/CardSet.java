package njan.flashcardseditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan
 */
public class CardSet {
    private static final String ns = null;
    private static final String CARD_SET_NAME = "CardSet";
    private static final String CARD_NAME = "Card";
    private static final String ID_ATTRIBUTE = "ID";
    private static final String FRONT_NAME = "Front";
    private static final String BACK_NAME = "Back";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String TEXT_SIZE_ATTRIBUTE = "textSize";

    private static final String DEFAULT_NAME = "unnamed";
    public static final float DEFAULT_TEXT_SIZE = 32;

    private String name;
    private float textSize;
    private final List<Card> cards;

    public CardSet(String name, float textSize, List<Card> cards) {
        this.name = name;
        this.textSize = textSize;
        this.cards = cards;
    }
    
    public CardSet(File file) {
        name = DEFAULT_NAME;
        textSize = DEFAULT_TEXT_SIZE;
        cards = new ArrayList<>();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Element root = doc.getDocumentElement();
            
            // get set name
            String nameAttr = root.getAttribute(NAME_ATTRIBUTE);
            if (nameAttr != null)
                name = nameAttr;
            
            // get text size
            String textSizeAttr = root.getAttribute(TEXT_SIZE_ATTRIBUTE);
            if (textSizeAttr != null)
                textSize = Float.parseFloat(textSizeAttr);
            
            // read all cards
            NodeList nodes = root.getElementsByTagName(CARD_NAME);
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element cardElement = (Element) node;
                    
                    // get id
                    String id = cardElement.getAttribute(ID_ATTRIBUTE);
                    
                    // get front and back
                    String front = cardElement.getElementsByTagName(FRONT_NAME).item(0).getTextContent();
                    String back = cardElement.getElementsByTagName(BACK_NAME).item(0).getTextContent();
                    
                    // add card to set
                    cards.add(new Card(front, back, id));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            name = null;
            cards.clear();
        }
    }
    
    /**
     * Save set to a xml file
     * @param file
     */
    public void save(File file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            // create the root element
            Element root = doc.createElement(CARD_SET_NAME);
            root.setAttribute(NAME_ATTRIBUTE, name);
            root.setAttribute(TEXT_SIZE_ATTRIBUTE, String.valueOf(textSize));
            
            for (Card card : cards) {
                // card node
                Element cardElement = doc.createElement(CARD_NAME);
                
                // id
                if (card.id != null && !card.id.equals(""))
                    cardElement.setAttribute(ID_ATTRIBUTE, card.id);
                 
                // front node
                Element frontElement = doc.createElement(FRONT_NAME);
                frontElement.appendChild(doc.createTextNode(card.front));
                cardElement.appendChild(frontElement);
                
                // back node
                Element backElement = doc.createElement(BACK_NAME);
                backElement.appendChild(doc.createTextNode(card.back));
                cardElement.appendChild(backElement);
                
                // add to root node
                root.appendChild(cardElement);
            }

            doc.appendChild(root);

            // write to file
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            var fos = new FileOutputStream(file);
            tr.transform(new DOMSource(doc), new StreamResult(fos));
            
            fos.close();
            
        } catch (TransformerException | IOException | ParserConfigurationException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Could not save!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getName() {
        return name;
    }

    public float getTextSize() {
        return textSize;
    }
    
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public void setName(String newName) {
        name = newName;
    }

    public boolean isEmpty() { return cards.isEmpty(); }
}
