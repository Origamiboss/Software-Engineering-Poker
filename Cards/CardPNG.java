package Cards;

import Cards.*;

import javax.swing.*;

public class CardPNG extends JPanel{
	
	JLabel number;
	JLabel suit;
	
	public CardPNG(){
		//Get the Images into an Image Icon
		
		
		//put the Image Icon into the JLabel
		this.suit = new JLabel(new ImageIcon("Cards/Suit/Heart.png"));
		//add the Labels to the panel
		this.add(this.suit);
		//this.add(this.number);
	}
	//Suit: (Heart, Diamond, Clover, Spade) Number: (1-14) 11 Jack, 12 Queen, 13 King, 14 Joker
	public CardPNG(String suit, int number){
		
		//Get the Images into an Image Icon
		switch(suit) {
			case "Clover":
				this.suit = new JLabel(new ImageIcon("Cards/Suit/Clover.png"));
				break;
			case "Diamond":
				this.suit = new JLabel(new ImageIcon("Cards/Suit/Diamond.png"));
				break;
			case "Heart":
				this.suit = new JLabel(new ImageIcon("Cards/Suit/Heart.png"));
				break;
			case "Spade":
				this.suit = new JLabel(new ImageIcon("Cards/Suit/Spade.png"));
				break;
			default:
				this.suit = new JLabel(new ImageIcon("Cards/Suit/Heart.png"));
				break;
				
		}
		
		
		//put the Image Icon into the JLabel
		
		//add the Labels to the panel
		this.add(this.suit);
		//this.add(this.number);
	}

}
