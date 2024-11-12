package Game;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class TestMainPage {
	TestMainPage(){
		JFrame j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setLayout(new BorderLayout());
	    j.setSize(500,500);
	    
	    j.add(new MainPageController());
	    
	    j.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new TestMainPage();
	}
}
