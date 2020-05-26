package CucumberTest.CucumberTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as a singleton to send a single PowerShell command, then split the output and error stream of the 
 * command into a list for the user to evaluate. Most of the remote command execution will be handled in this class 
 * under the "sendRemoteCommand" method.
 */
public class PsCommand {
	
	
	public static List<String> sendCommand(String command) throws IOException {
		
		// Executing the PowerShell command. This works on Windows and Linux.
		Process p = Runtime.getRuntime().exec("powershell -command " + command);
		
		// Getting the results
		p.getOutputStream().close();
		
		String line;
        /*
         * The StringBuilder class that is mutable and is designed to solve the performance issue with normal String 
         * concatenation. When you concatenate many Strings, use StringBuilder to gain performance.
         */
		StringBuilder Output = new StringBuilder(); 
		StringBuilder Error = new StringBuilder(); 
        
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = stdout.readLine()) != null) {
			//System.out.println(line);
			Output.append(line + "\n");
		}
		stdout.close();
		
		BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stderr.readLine()) != null) {
			//System.out.println(line);
			Error.append(line + "\n");
		}
		stderr.close();
		
        //Creates a new list.
        List<String> list = new ArrayList<String>();
        
        //Adds the standard output and error stream to the new list.
        list.add(Output.toString());
        list.add(Error.toString());
        
        // Terminal output as a list.
        return list;
        
	}
	

	public static List<String> sendRemoteCommand(String command) throws Throwable {
		
		List<String> list = sendRemoteCommand(command, 
				CommonCliSteps.psConnectionValues.get("psHost"), 
				CommonCliSteps.psConnectionValues.get("psLoginName"), 
				CommonCliSteps.psConnectionValues.get("psLoginPassword"), 
				CommonCliSteps.psConnectionValues.get("psPort"), 
				30);
		
		return list;
	}
	

	public static List<String> sendRemoteCommand(String command, int timeOut) throws Throwable {
		
		List<String> list = sendRemoteCommand(command, 
				CommonCliSteps.psConnectionValues.get("psHost"), 
				CommonCliSteps.psConnectionValues.get("psLoginName"), 
				CommonCliSteps.psConnectionValues.get("psLoginPassword"), 
				CommonCliSteps.psConnectionValues.get("psPort"), 
				timeOut);
		
		return list;
	}
	
	
	public static List<String> sendRemoteCommand(String command, String host, String loginName, String loginPassword, 
			String port, int timeOut) throws Throwable {
		// ToDo: Implement the timeout feature. I think this needs to be done in the PowerShell command.
		
		// create a StringBuilder object  using StringBuilder() constructor 
        StringBuilder connect  = new StringBuilder(); 
		
		//Convert plain text to secure strings. It is used with ConvertFrom-SecureString and Read-Host. 
		connect.append(String.format("$secpwd = ConvertTo-SecureString '%s' -AsPlainText -Force; ", loginPassword));
		//Offers a centralized way to manage usernames, passwords, and credentials.
		connect.append(String.format("$cred = New-Object System.Management.Automation.PSCredential ({}, "
				+ "$Secure_String_Pwd); ", loginName));
		
		if (port == null) {
			connect.append(String.format("Invoke-Command –ComputerName %s -Credential $cred -ScriptBlock { $s }", host, 
					command)); 
		} else {
			connect.append(String.format("Invoke-Command -ComputerName %s -Port %s -Credential $cred -ScriptBlock  "
					+ "{ $s }", host, port, command)); 
		}
		
		List<String> list = sendCommand(connect.toString());
		
		return list;
	}

}
