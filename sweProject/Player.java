package sweProject;

public class Player {
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
	
	public void changeWealth(int amountChanged) {
		wealth += amountChanged;
	}
	
	public int getWealth() {
		return wealth;
	}
	
}
