package NewAccountUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import sweProject.Database;

public class NewAccountTest {
	NewAccountTest(){
		JFrame j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setLayout(new BorderLayout());
	    j.setSize(500,500);
	    
	    j.add(new NewAccountController(new Database()));
	    
	    j.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new NewAccountTest();
	}
}
