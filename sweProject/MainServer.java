package sweProject;

public class MainServer {
	private Player player;
	
	public Player createAccount(LoginData loginData) {
		Player player = new Player();
		return player;
	}
	
	public void deleteAccount(Player player) {
		
	}
	
	public boolean validateLogin(LoginData loginData) {
		return true;
	}
}