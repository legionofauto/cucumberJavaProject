package CucumberTest.CucumberTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.cucumber.java.After;

/**
* 
*
*/
public class PsStream {
	
    //Runtime allows the application to interface with the environment in which the application is running.
    public Runtime r = Runtime.getRuntime();
    
    //The class Process provides methods for performing input from the process.
    Process p;
    
    // Lets the method know if the stream is active of not.
    static boolean streamIsActive = false;
    
    //Container for the processor ID.
    public String streamPID;
    
    //The command used for the method. 
    String command;
	
	/*
	 * Executes the parent method.
	 */
    public List<String> sendCommand(String comString) throws Throwable {
    	List<String> list = sendCommand(comString, 1);
    	return list;
    }
    
    
    public List<String> sendCommand(String comString, int waitTime) throws Throwable{
    	waitTime = waitTime * 1000;
		
        // If the Powershell Stream is not active yet
        if (!streamIsActive) {
        	// The session is initialized.
            this.initializePowerShell();
	    } 

        InputStream std, err;
        OutputStream out;
        std = p.getInputStream(); // Used for reading terminal text
        out = p.getOutputStream(); // Used for writing commands
        err = p.getErrorStream(); // Used for reading errors
	    

    	// Adds the \n to the end of the string to emulate the user pressing the "enter" button.
        String comNewString = comString + "\n";
		
        /*
         * The StringBuilder class that is mutable and is designed to solve the performance issue with normal String 
         * concatenation. When you concatenate many Strings, use StringBuilder to gain performance.
         */
		StringBuilder Output = new StringBuilder(); 
		StringBuilder Error = new StringBuilder(); 
        
	    try {
	    	out.write(comNewString.getBytes()); // Writes the commands to terminal in binary.
	    	out.flush(); // Flushes all the buffers in a chain of Writers and OutputStreams.
        
            Thread.sleep(waitTime); // Time in milliseconds to pause for processing.
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
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
	    // While there is information in the error stream still buffered.
        while (err.available() > 0) {
        	value2 = err.read();
            //System.out.print((char) err.read()); // Prints the error output for debugging purposes
            Error.append(((char) value2)); // Saves the output one line at a time.
        }
		
        // Cleanup output steps.
        String cwd = System.getProperty("user.dir");
        
    	// Detecting the operating system using os.name System property.
     	String os = System.getProperty("os.name").toLowerCase();
        
     	String newOutput = Output.toString();
    	if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
    		// To do: Add Linux replacements
    	} else {	
    		// Crazy amount of escape characters
	        cwd = cwd.replaceAll("\\\\", "\\\\\\\\");
	        
	        // Removes the terminal prompt from Standard Out for clean output.
	        newOutput = newOutput.replaceFirst("^PS " + cwd + ".*\\n", "");
	        newOutput = newOutput.replaceFirst("\\nPS " + cwd + ".*$", "");
	        //Output = Output.replaceFirst("^" + comNewString, "");
    		
    	}

		
        //Creates a list.
        List<String> list = new ArrayList<String>();
        
        //Adds the standard output and error stream to the new list.
        list.add(newOutput);
        list.add(Error.toString());
        
        // Terminal output as a list.
        return list;
        
    }


    /**
     * Used to start the PowerShell session through Command Prompt. This method will work on both Windows
     * and Linux operating systems. 
     */
    private void initializePowerShell() throws IOException, InterruptedException{
    	
    	// Detecting the operating system using os.name System property.
     	String os = System.getProperty("os.name").toLowerCase();
		
     	// Makes the method Windows and Linux friendly.
    	if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
    		//If you are running this on linux.
    		command = "powershell -nologo -noexit -command $pid"; 
    	} else {
    		//Else you are running this on Windows.
    		command = "powershell -ExecutionPolicy Bypass -NoExit -NoProfile -Command $pid"; 
    	}

		try {
			// Runs the command.
			p = r.exec(command);
			
			//Lets the scenario know that a PowerShell stream is active.
			streamIsActive = true;
			
			//Time in milliseconds to pause for processing.
	        Thread.sleep(500); 
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        InputStream std, err;
        std = p.getInputStream(); // Used for reading terminal text
        err = p.getErrorStream(); // Used for reading errors
		
        /*
         * The StringBuilder class that is mutable and is designed to solve the performance issue with normal String 
         * concatenation. When you concatenate many Strings, use StringBuilder to gain performance.
         */
		StringBuilder Output = new StringBuilder(); 
		StringBuilder Error = new StringBuilder(); 
		
        int value;
        //Collects the output information. 
        while (std.available() > 0) {
            value = std.read();
            Output.append(((char) value));
        }
        
        //Collects the error information. 
        while (err.available() > 0) {
            value = err.read();
            Error.append(((char) value));
        }
        
        //Kills the process if an error occurs.
        assert Error.toString().isEmpty() : "An error occurred when initializing PowerShell";
        
        //Saves the process ID to a string variable.
        streamPID = Output.toString();
        
    }
    
    
    /**
     * Kills the process for the PowerShell session. The @After symble will kill the process after the scenario is 
     * completed.
     */
    @After public void close() {
    	String killProcessCommand;
    	
    	// Detecting the operating system using os.name System property.
     	String os = System.getProperty("os.name").toLowerCase();
     	
    	// A Command Prompt command to target a specific Process ID to terminate. 
    	if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
    		//If you are running this on Linux.
    		killProcessCommand = String.format("kill -9 %s", streamPID); 
    	} else {
    		//If you are running this on Windows.
    		killProcessCommand = String.format("taskkill /F /PID %s", streamPID); 
    	}
    	
	    try {
	    	//Sends the kill command to the OS.
	    	Runtime.getRuntime().exec(killProcessCommand);
	    	
	    	//Lest the scenario know that the stream is closed. 
	    	streamIsActive = false;
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	    
    }
    

}