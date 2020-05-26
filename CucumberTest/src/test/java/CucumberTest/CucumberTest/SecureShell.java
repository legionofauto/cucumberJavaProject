package CucumberTest.CucumberTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.cucumber.java.After;


/**
* Plink is a command-line connection tool similar to UNIX ssh. It is mostly used for automated operations, such as 
* making CVS access a repository on a remote server.  
* 
* See more here:
* https://www.ssh.com/ssh/putty/putty-manuals/0.68/Chapter7.html
*/
public class SecureShell {

    //Runtime allows the application to interface with the environment in whichthe application is running.
    public Runtime r = Runtime.getRuntime();
    
    //The class Process provides methods for performing input from the process.
    Process p;

    /**
     * @param username: Your login username
     * @param password: Your login password.
     * @param host: The hostname or IP address of the node you SSH to.
     * @return: True or false on the connection status to the host.
     * @throws Throwable Indicate that this method may throw mentioned exceptions.
     */
    public List<String> connect(String username, String password, String host) throws Throwable{
    	List<String> sshOutput = connect(username, password, host, 22, "ssh", 2);
        return sshOutput;
    }

    /**
     * @param username: Your login username
     * @param password: Your login password.
     * @param host: The hostname or IP address of the node you SSH to.
     * @return: True or false on the connection status to the host.
     * @throws Throwable Indicate that this method may throw mentioned exceptions.
     */
    public List<String> connect(String username, String password, String host, int port) throws Throwable{
    	List<String> sshOutput = connect(username, password, host, port, "ssh", 2);
        return sshOutput;
    }

    /**
     * @param username: Your login username
     * @param password: Your login password.
     * @param host: The hostname or IP address of the node you SSH to.
     * @param port: The desired port for the connection. If nothing is used, port 22 will be assigned.
     * @param version: the version (1 or 2). Only applies to SSH connections and defaults to version 2. (Plink only)
     * @return: True or false on the connection status to the host.
     * @throws Throwable Indicate that this method may throw mentioned exceptions.
     */
    public List<String> connect(String username, String password, String host, int port, String connectionType) throws Throwable{
    	List<String> sshOutput = connect(username, password, host, port, connectionType, 2);
        return sshOutput;
    }

    /**
     * Creates a PuTTY terminal connection to a remote host.
     *
     * @param username: Your login username
     * @param password: Your login password.
     * @param host: The hostname or IP address of the node you SSH to.
     * @param port: The desired port for the connection. If nothing is used, port 22 will be assigned.
     * @param version: the version (1 or 2). Only applies to SSH connections and defaults to version 2. (Plink only)
     * @param connectionType: Accepts "ssh", "telnet", "rlogin", "raw", or "serial" only.
     * @return: True or false on the connection status to the host.
     * @throws Throwable Indicate that this method may throw mentioned exceptions.
     */
    public List<String> connect(String username, String password, String host, int port, String connectionType, int version) throws Throwable{
        
		//Loads the properties file information.
		Properties project = CommonMethods.readPropertiesFile("project.properties");
		
        /*
         * The StringBuilder class that is mutable and is designed to solve the performance issue with normal String 
         * concatenation. When you concatenate many Strings, use StringBuilder to gain performance.
         */
        StringBuilder connectString = new StringBuilder(); 
        
    	// Detecting the operating system using os.name System property.
     	String os = System.getProperty("os.name").toLowerCase();
     	
    	// A Command Prompt command to target a specific Process ID to terminate. 
    	if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
    		//If you are running this on Linux.
	        //If the connection type is not "telnet", "rlogin", "raw", or "serial".
	        if(!connectionType.equals("telnet") || !connectionType.equals("rlogin") || !connectionType.equals("raw") || 
	        		!connectionType.equals("serial")) {
	        	//Then the connection type is ssh no matter what you want.
	            connectString.append("ssh ");
	        } else {
	            connectString.append(connectionType + " ");
	        }
	        
    		connectString.append("-L " + host); 
    		connectString.append(" -l " + username); 
	        
	        //If no port is outlined,
	        if(port==0) {
	        	//The port will automatically be port 22.
	            connectString.append(" -p 22");
	        } else {
	            connectString.append(" -p " + Integer.toString(port));
	        }
    	} else {
        
	        //Starts the string off with the path to the plink.exe.
	        connectString.append(project.getProperty("plinkExe"));
	        
	        //If the connection type is not "telnet", "rlogin", "raw", or "serial".
	        if(!connectionType.equals("telnet") || !connectionType.equals("rlogin") || !connectionType.equals("raw") || 
	        		!connectionType.equals("serial")) {
	        	//Then the connection type is ssh no matter what you want.
	            connectString.append(" -ssh ");
	        } else {
	            connectString.append(" -" + connectionType + " ");
	        }
	        
	        connectString.append(host);
	        connectString.append(" -l " + username);
	        
	        //If no port is outlined,
	        if(port==0) {
	        	//The port will automatically be port 22.
	            port = 22;
	            connectString.append(" -P 22");
	        } else {
	            connectString.append(" -P " + Integer.toString(port));
	        }
	        
	        //If no version is outlined,
	        if(version==0) {
	            //The version will automatically be 2.
	            connectString.append(" -2");
	        } else {
	        	connectString.append(" -" + Integer.toString(version));
	        }
	        
	        connectString.append(" -v ");
	        
    	}
        
        // If the below command fails, Check the File Path for plink.exe.
        p = r.exec(connectString.toString());

        InputStream std, err;
        std = p.getInputStream(); // for reading terminal text
        err = p.getErrorStream();

        Thread.sleep(1000);
        
        //Checks if the "trusted host" prompt appears when connecting to the host.
        boolean trustedHostFlag = this.checkIfTrustedHost(err);
        
        //If the user is prompted for the trusted host key,
        if(!trustedHostFlag){
        	//then the user types "y" to accept the host key.
        	this.sendCommand("y");
        }
		
        /*
         * The StringBuilder class that is mutable and is designed to solve the performance issue with normal String 
         * concatenation. When you concatenate many Strings, use StringBuilder to gain performance.
         */
		StringBuilder Output = new StringBuilder(); 
		StringBuilder Error = new StringBuilder(); 
		
	    // While there is information still buffered.
	    while (std.available() > 0) {
    		int value;
    	    // While there is information in the standard output stream still buffered.
	    	while (std.available() > 0) {
	    		value = std.read();
		        //System.out.print((char) value); // Prints terminal output for debugging purposes
		        Output.append(((char) value)); // Saves the output, as a string, one character at a time.
	    	}
	    	
	    	//Give the buffer time to refill if there is anything left.
            Thread.sleep(10);
	    	
		}
	    
	    int value2;
	    // While there is information in the Error stream still buffered.
        while (err.available() > 0) {
        	value2 = err.read();
            //System.out.print((char) err.read()); // Print to terminal for debugging.
            // Takes the binary Error Stream output and puts it into ASCII Control.
            Error.append(((char) value2));
        }

        List<String> sshOutput = new ArrayList<String>();
        
        //Sends the password
        sshOutput = this.sendCommand(password);
        
        //Returns the connection status.
        return sshOutput;
    }

    
    /**
     * Takes an SSH terminal command as a string then outputs what the SSH terminal spits out as a string.
     * 
     * @param comString: The command to be sent.
     * @return The information that the command derives.
     */
    public List<String> sendCommand(String comString) {
        return sendCommand(comString, 1000);
    }


    /**
     * Takes an SSH terminal command as a string then outputs what the SSH terminal spits out as a string.
     *
     * @param comString: The command to be sent.
     * @param waitTime: Milliseconds to sleep to allow linux command to finish processing.
     * @return The information that the command derives.
     */
    public List<String> sendCommand(String comString, int waitTime) {

        InputStream std, err;
        OutputStream out;
        std = p.getInputStream(); // Used for reading terminal text
        out = p.getOutputStream(); // Used for writing commands
        err = p.getErrorStream(); // Used for reading errors

        //Output containers.
		StringBuilder Output = new StringBuilder(); 
		StringBuilder Error = new StringBuilder(); 
        
        // Adds the \n to the end of the string to emulate the user pressing the "enter" button.
        String comNewString = comString + "\n";

        try {
            out.write(comNewString.getBytes()); //Write commands to terminal
            out.flush();

            Thread.sleep(waitTime); //Time to pause in milliseconds for processing.
            
    	    // While there is information still buffered.
    	    while (std.available() > 0) {
        		int value;
        	    /*
        	     * This is a loop where all the information in the buffer gets read into the Output container. Just 
        	     * because the buffer is empty, doesn't mean their is no more data to be had. The loop waits 10 
        	     * milliseconds for the buffer to fill back up (if there is anything). If their is more data, the 
        	     * process will continue, else, moves on to the error output.
        	     */
    	    	while (std.available() > 0) {
    	    		value = std.read();
    		        //System.out.print((char) value); // Prints terminal output for debugging purposes
    		        Output.append(((char) value)); // Saves the output, as a string, one character at a time.
    	    	}
    	    	
    	    	//Give the buffer time to refill if there is anything left.
                Thread.sleep(10);
    	    	
    		}
    	    
    	    int value2;
    	    // While there is information in the Error stream still buffered.
            while (err.available() > 0) {
            	value2 = err.read();
                //System.out.print((char) err.read()); // Print to terminal for debugging.
                // Takes the binary Error Stream output and puts it into ASCII Control.
                Error.append(((char) value2));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //Creates a list.
        List<String> list = new ArrayList<String>();
        
        //Adds the standard output and error stream to the new list.
        list.add(Output.toString());
        list.add(Error.toString());
        
        // Terminal output as a list.
        return list;
        
    }
    
    
    /**
     * Checks to see if host is trusted already or not. Returns true if it is and false if not.
     *
     * @param err: Error stream to feed into the method.
     * @return: true or false to see if it is a recognized host
     * @throws IOException: An exception is an event, which occurs during the execution of a program, that disrupts the 
     * 	normal flow of the program's instructions.
     */
    private boolean checkIfTrustedHost(InputStream err) throws IOException{
    	
    	//Container for the error output.
		StringBuilder Error = new StringBuilder(); 
		
		int value;
	    // While there is information in the Error stream still buffered.
        while (err.available() > 0) {
        	value = err.read();
            //System.out.print((char) value)); // std reads in integers then needs to be converted to characters
            Error.append(((char) value)); // concatenate characters to form string for terminal output
        }
        
        // Checks to see if the server's host key is not cached in the registry.
        return CommonMethods.regexFound("host key is not cached in the registry", Error.toString());
    }
    
    
    //Destroys the SSH session. 
    @After public void close() {
    	
	    try {
	    	//Sends the kill command to the OS.
	    	Runtime.getRuntime().exec("taskkill /im plink.exe");
	    } catch (Exception e) {
			e.printStackTrace();
	    }
    }

}