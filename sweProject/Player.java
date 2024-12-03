package sweProject;

import java.io.Serializable;

public class Player implements Serializable{
	private LoginData loginData;
	private int wealth;
	
	public Player() {
		wealth = 0;
		loginData = new LoginData();
	}
	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
	}
	
	public LoginData getLoginData() {
		return loginData;
	}
	
	public void setWealth(int amountChanged) {
		wealth += amountChanged;
	}
	public int getWealth() {
		return wealth;
	}
	
}
