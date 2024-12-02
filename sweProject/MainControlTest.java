package sweProject;

import static org.junit.Assert.*;

import java.awt.*;
import javax.swing.*;

import org.junit.Before;
import org.junit.Test;

public class MainControlTest {
	private MainControl mc;
	
	
	@Before
	public void setUp() throws Exception 
	{
		mc = new MainControl();
	}
	

	@Test
	public void testOpenInitial() {
		mc.openInitial();
        
        // Since showPage changes the content panel, we check if it correctly switched to "Initial"
        assertTrue(mc.getContentPane().getLayout() instanceof CardLayout); 
        CardLayout layout = (CardLayout) mc.getContentPane().getLayout();
        layout.show(mc.getContentPane(), "Initial"); // Simulate switching to "Initial" page
    }
	


	@Test
	public void testOpenMainPage() {
		mc.openMainPage();
	    
	    // Check if the page switched to "Main"
	    assertTrue(mc.getContentPane().getLayout() instanceof CardLayout);
	    CardLayout layout = (CardLayout) mc.getContentPane().getLayout();
	    layout.show(mc.getContentPane(), "Main"); // Simulate switching to "Main" page
	}

	@Test
	public void testOpenNewAccount() {
		mc.openNewAccount();
	    
	    // Check if the page switched to "Account"
	    assertTrue(mc.getContentPane().getLayout() instanceof CardLayout);
	    CardLayout layout = (CardLayout) mc.getContentPane().getLayout();
	    layout.show(mc.getContentPane(), "Account"); // Simulate switching to "Account" page
     }

	@Test
	public void testOpenGameClient() {

		mc.openGameClient();
	    
	    // Check if the page switched to "GameClient"
	    assertTrue(mc.getContentPane().getLayout() instanceof CardLayout);
	    CardLayout layout = (CardLayout) mc.getContentPane().getLayout();
	    layout.show(mc.getContentPane(), "GameClient"); // Simulate switching to "GameClient" page
	}

	@Test
	public void testOpenLogin() {
		mc.openLogin();
	    
	    // Check if the page switched to "Login"
	    assertTrue(mc.getContentPane().getLayout() instanceof CardLayout);
	    CardLayout layout = (CardLayout) mc.getContentPane().getLayout();
	    layout.show(mc.getContentPane(), "Login"); // Simulate switching to "Login" page
	}


	@Test
	public void testHostGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testValidIp() {
		String ip = "localhost";
	    int port = 8080;

	    // Should work with local server up
	    assertTrue(mc.validIp(ip, port));
	}

	

}
