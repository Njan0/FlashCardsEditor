package njan.flashcardseditor;

/**
 *
 * @author Jan
 */
public class Card {
    public String id;
    public String front;
    public String back;

    public Card(String front, String back) {
        this(front, back, null);
    }
    
    public Card(String front, String back, String id) {
        this.id = id;
        this.front = front;
        this.back = back;
    }

    @Override
    public String toString() {
        if (id != null && !id.equals(""))
            return id;
        
        return front;
    }
}
