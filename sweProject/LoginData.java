package sweProject;

import java.io.Serializable;

public class LoginData implements Serializable{
	private String purpose;
	private String username;
	private String password;
	
	public String getPurpose() {
		return purpose;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
