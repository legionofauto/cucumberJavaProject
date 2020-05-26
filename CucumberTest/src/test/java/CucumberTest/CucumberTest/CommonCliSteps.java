package CucumberTest.CucumberTest;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import io.cucumber.java.en.Given;

public class CommonCliSteps {
	
	//PsStream class put to an object.
	PsStream psObj = new PsStream();
	
	//SSH class put to an object.
	SecureShell sshObj = new SecureShell();
	
	//PowerShell singleton command output.
	List<String> psCmdOutput;

	//PowerShell stream command output.
	List<String> psStreamOutput;

	//SSH stream command output.
	List<String> sshOutput;
	
	//A dictionary used for PowerShell singleton values.
	static Dictionary<String, String> psConnectionValues;
	
	
	@Given("^the \"([^\"]*)\" host is online$")
	public void givenTheHostIsOnline(String host) throws Throwable {
		
		//Ping command string sent throw the 
		psCmdOutput = PsCommand.sendCommand(String.format("ping %s -n 1 -w 4", host));
		
		System.out.println(psCmdOutput.get(0));
		
		//Asserts that their is no errors when entering the ping command.
		assert psCmdOutput.get(1).isEmpty() : "An error occurred when pinging the host!";
		
		//Asserts the host is on-line.
		assert psCmdOutput.get(0).contains("Lost = 0") : String.format("Failed to see the '%s' host online", host);
		
	}
	
	/*
	 * Streaming PowerShell commands have to stay within this class. If used elsewhere, the input stream will close.
	 */
	public void streamPsCommand(String command) throws Throwable {
		psStreamOutput = psObj.sendCommand(command);
	}
	
	
	/*
	 * Just a test of streaming Powershell.
	 */
	@Given("^PowerShell works$")
	public void givenPowerShellWorks() throws Throwable {
		// The 'ls' command will produce an error if PowerShell was not initialized.
		psStreamOutput = psObj.sendCommand("ls;ls;ls;ls;ls;ls;ls;ls;ls;");
		
		//Prints the output stream so you can tell what is going on.
		System.out.println(psStreamOutput.get(0));
		
		//If there is an error, you'll be able to determine what went wrong. 
		assert psStreamOutput.get(1).isEmpty() : "PowerShell was not initialized!";
	}
	
	
	@Given("^the \"([^\"]*)\" user account establishes a new PowerShell Session to \"([^\"]*)\" host$")
	public void givenTheAuthorizedUserAccountEstablishesANewPowerShellSessionToValidHost(String user, String host) 
			throws Throwable {
		
		// create a StringBuilder object using StringBuilder() constructor 
        StringBuilder connect  = new StringBuilder(); 
        
		//Loads the properties file information.
		Properties userProp = CommonMethods.readPropertiesFile("user.properties");
		
		
		//Convert plain text to secure strings. It is used with ConvertFrom-SecureString and Read-Host. 
		connect.append(String.format("$secpwd = ConvertTo-SecureString '%s' -AsPlainText -Force; ", 
				userProp.getProperty(user + ".password")));
		
		//Offers a centralized way to manage usernames, passwords, and credentials.
		connect.append(String.format("$cred = New-Object System.Management.Automation.PSCredential ({}, "
				+ "$Secure_String_Pwd); ", userProp.getProperty(user)));
		
		//Creates a persistent connection to a local or remote computer.
		connect.append(String.format("New-PSSession –ComputerName %s -Credential $cred", host)); 
		
		psStreamOutput = psObj.sendCommand(connect.toString());
		
		//Checks the error stream for "Access denied" text.
		assert !psStreamOutput.get(1).contains("Access denied") : "The user was denied access to the host!";
		
		//Error stream is not empty.Access granted
		assert !psStreamOutput.get(1).isEmpty() : "An error occurred when connecting the host!";
		
	}
	
	
	@Given("^the \"([^\"]*)\" user account can WinRM to \"([^\"]*)\" host$")
	public void givenTheAuthorizedUserAccountCanWinRmToValidHost(String user, String host) throws Throwable {
		
        this.powerShellConnectionValues(user, host, 443);
		
		//Sends the command. All of the user properties will be processed on the PsCommand side. 
		psCmdOutput = PsCommand.sendRemoteCommand("ls");
		
		//Checks the error stream for "Access denied" text.
		assert psCmdOutput.get(1).contains("Access denied") : "The user was denied access to the host!";
		
		//Error stream is not empty.Access granted
		assert psCmdOutput.get(1).isEmpty() : "An error occurred when connecting the host!";
		
	}
	
	
	@Given("^the \"([^\"]*)\" user account can WinRM to \"([^\"]*)\" host on port (\\d+)$")
	public void givenTheAuthorizedUserAccountCanWinRmToValidHost(String user, String host, int port) throws Throwable {
		
		//Collects PowerShell remote connection information.
        this.powerShellConnectionValues(user, host, port);
		
		//Sends the command. All of the user properties will be processed on the PsCommand side. 
		psCmdOutput = PsCommand.sendRemoteCommand("ls");
		
		//Checks the error stream for "Access denied" text.
		assert psCmdOutput.get(1).contains("Access denied") : "The user was denied access to the host!";
		
		//Error stream is not empty.Access granted
		assert psCmdOutput.get(1).isEmpty() : "An error occurred when connecting the host!";
		
	}
	
	
	/*
	 * You'll use the connection steps above to establish connection values used to login to a remote host. These values
	 * will be used every time you use the "sendRemoteCommand" method until you change the values either manually or
	 * through repeating the connection steps to replace them.
	 */
	public void powerShellConnectionValues(String user, String host, int port) throws Throwable {
		
		//Starts a new PowerShell connection values dictionary. 
        psConnectionValues = new Hashtable<String, String>();
        
		//Loads the properties file information.
		Properties prop = CommonMethods.readPropertiesFile("user.properties");
		
		//Adds the password to the list for purging after the scenario.
		CommonMethods.cleartextPasswords.add(prop.getProperty(user + ".password"));
		
		//Saves the information to use in the PsCommand.sendRemoteCommand commands.
		psConnectionValues.put("psLoginName", prop.getProperty(user));
		psConnectionValues.put("psLoginPassword", prop.getProperty(user + ".password"));
		psConnectionValues.put("psHost", host);
		psConnectionValues.put("psPort", Integer.toString(port));
		
	}
	
	
	@Given("^the \"([^\"]*)\" user account establishes an SSH connection to \"([^\"]*)\" host$")
	public void givenTheAuthorizedUserAccountEstablishesAnSshConnectionToValidHost(String user, String host) 
			throws Throwable {
        
		//Loads the properties file information.
		Properties prop = CommonMethods.readPropertiesFile("user.properties");
		
		//
		sshOutput = sshObj.connect(prop.getProperty(user), prop.getProperty(user + ".password"), host);
		
		//Checks the error stream for "Access denied" text.
		assert !sshOutput.get(1).contains("Access denied") : "The user was denied access to the resource!";
		
		//Error stream is not empty.Access granted
		assert !sshOutput.get(1).isEmpty() : "An error occurred when connecting the resource!";
		
		//Checks the output for "Access granted" string in the Standard Output.
		assert sshOutput.get(0).contains("Access granted") : "The user was not granted access to the resource!";
		
	}
	
	
	public void streamSshCommand(String command) throws Throwable {
		sshOutput = sshObj.sendCommand(command);
	}
	
	
	/*
	 * Elevates the login user to root. 
	 * 
	 * @param user: The user account password in your properties file you want to use to sudo with.
	 */
	@Given("^the \"([^\"]*)\" user elevates to root$")
	public void givenTheAuthorizedUserElevatesToRoot(String user) throws Throwable {
		Properties prop = CommonMethods.readPropertiesFile("user.properties");
		//Adds the password to the list for purging after the scenario.
		CommonMethods.cleartextPasswords.add(prop.getProperty(user + ".password"));
		
		sshOutput = sshObj.sendCommand("sudo su -");
		assert !sshOutput.get(1).isEmpty() : "An error occurred when connecting the resource!";
		
		sshOutput = sshObj.sendCommand(prop.getProperty(user + ".password"));
		assert !sshOutput.get(1).contains("Access denied") : "The user was denied elevation to root!";
		
		sshOutput = sshObj.sendCommand(prop.getProperty("whoami"));
		assert sshOutput.get(0).equals("root") : "The user was denied elevation to root!";
	}


}
