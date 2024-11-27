package sweProject;

import java.io.Serializable;

public class GameData implements Serializable {
	private int bettedMoney;
	private int cardsSwapped;
	private int totalMoneyAmount;
	private String username;
	
	public int getBettedMoney() {
		return bettedMoney;
	}
	public int getCardsSwapped() {
		return cardsSwapped;
	}
	public int getTotalMoneyAmount() {
		return totalMoneyAmount;
	}
	public void setBettedMoney(int t) {
		bettedMoney = t;
	}
	public void setCardsSwapped(int t) {
		cardsSwapped = t;
	}
	public void setTotalMoneyAmount(int t) {
		totalMoneyAmount = t;
	}
	
	public void setUsername(String s) {
		username = s;
	}
	public String getUsername() {
		return username;
	}
	
	//update the gamedata
	public void update(GameData gd) {
		bettedMoney = gd.getBettedMoney();
		cardsSwapped = gd.getCardsSwapped();
		totalMoneyAmount = gd.getTotalMoneyAmount();
	}
	
}
