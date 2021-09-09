/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
