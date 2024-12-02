package sweProject;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;

public class MainControlTest {
	private MainControl mc;
    private ByteArrayOutputStream outputStreamCaptor;
	
	
	@Before
	public void setUp() throws Exception 
	{
		mc = new MainControl();
		 // Setup to capture System.out output
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
	}
	
	@Test
	public void testMainControl() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenInitial() {
		// Call the method under test
        mc.openInitial();

        // Verify that showPage was called with the correct argument ("Initial")
        String expectedOutput = "Displaying page: Initial\n";
        assertEquals(expectedOutput, outputStreamCaptor.toString());
	}


	@Test
	public void testOpenMainPage() {
		// Call the method under test
        mc.openMainPage();

        // Verify that showPage was called with the correct argument ("Initial")
        String expectedOutput = "Displaying page: Main\n";
        assertEquals(expectedOutput, outputStreamCaptor.toString());
	}

	@Test
	public void testOpenNewAccount() {
		// Call the method under test
        mc.openNewAccount();

        // Verify that showPage was called with the correct argument ("Initial")
        String expectedOutput = "Displaying page: Account\n";
        assertEquals(expectedOutput, outputStreamCaptor.toString());	
     }

	@Test
	public void testOpenGameClient() {

		// Call the method under test
        mc.openGameClient();

        // Verify that showPage was called with the correct argument ("Initial")
        String expectedOutput = "Displaying page: GameClient\n";
        assertEquals(expectedOutput, outputStreamCaptor.toString());
	}

	@Test
	public void testOpenLogin() {
		// Call the method under test
        mc.openLogin();

        // Verify that showPage was called with the correct argument ("Initial")
        String expectedOutput = "Displaying page: Login\n";
        assertEquals(expectedOutput, outputStreamCaptor.toString());
	}

	@Test
	public void testSetPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayer() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

}
