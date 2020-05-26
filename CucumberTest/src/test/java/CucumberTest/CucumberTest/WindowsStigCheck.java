package CucumberTest.CucumberTest;

import java.util.List;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WindowsStigCheck {

	PsStream psObj = new PsStream();
	
	List<String> psCmdOutput;
	

	@When("^the Windows Local Security Authority registry information is retrieved$")
	public void givenTheWindowsLocalSecurityAuthroityRegistryInformationIsRetrieved() throws Throwable {
		this.psCmdOutput = PsCommand.sendCommand(
				"Get-ItemProperty -Path HKLM:\\SYSTEM\\CurrentControlSet\\Control\\Lsa\\");
		assert this.psCmdOutput.get(1).isEmpty() : "PowerShell was not initialized!";
	}
	
	
	/*
	 * Follows STIG rule 'SV-78287r1_rule'.
	 * 
	 * Network security: Do not store LAN Manager hash value on next password change" to "Enabled"
	 */
	@Then("^Windows is configured to not store LAN Manager hash values$")
	public void thenWindowsIsConfiguredToNotStoreLanManagerHashValues() throws Throwable {

		String regex = "NoLmHash\\s+: 1";
		assert CommonMethods.regexFound(regex, this.psCmdOutput.get(0)) : 
			"Windows OS is not configured in accordance with STIG rule SV-78287r1_rule!";
	}
	

	@When("^the Windows NT Terminal Services registry information is retrieved$")
	public void givenTheWindowsNtTerminalServicesRegistryInformationIsRetrieved() throws Throwable {
		this.psCmdOutput = PsCommand.sendCommand(
				"Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\Policies\\Microsoft\\Windows NT\\Terminal Services\\'");
		assert this.psCmdOutput.get(1).isEmpty() : "PowerShell was not initialized!";
	}
	
	
	/*
	 * Follows STIG rule 'SV-78141r1_rule'.
	 * 
	 * Remote assistance allows another user to view or take control of the local session of a user. Solicited 
	 * assistance is help that is specifically requested by the local user. This may allow unauthorized parties access 
	 * to the resources on the computer. 
	 */
	@Then("^Windows is configured to disable Solicited Remote Assistance$")
	public void thenWindowsIsConfiguredToDisableSolicitedRemoteAssistance() throws Throwable {

		String regex = "fAllowToGetHelp\\s+: 0";
		assert CommonMethods.regexFound(regex, this.psCmdOutput.get(0)) : 
			"Windows OS is not configured in accordance with STIG rule SV-78287r1_rule!";
	}
	

	@When("^the Windows Installer registry information is retrieved$")
	public void givenTheWindowsInstallerRegistryInformationIsRetrieved() throws Throwable {
		this.psCmdOutput = PsCommand.sendCommand(
				"Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\Policies\\Microsoft\\Windows\\Installer\\'");
		
		assert this.psCmdOutput.get(1).isEmpty() : "PowerShell was not initialized!";
	}
	
	
	/*
	 * Follows STIG rule 'SV-78141r1_rule'.
	 * 
	 * Standard user accounts must not be granted elevated privileges. Enabling Windows Installer to elevate privileges 
	 * when installing applications can allow malicious persons and applications to gain full control of a system. 
	 */
	@Then("^Windows Always install with elevated privileges config item is disabled$")
	public void thenWindowsAlwaysInstallWithElevatedPrivilegesConfigItemIsDisabled() throws Throwable {

		String regex = "AlwaysInstallElevated\\s+: 0";
		assert CommonMethods.regexFound(regex, this.psCmdOutput.get(0)) : 
			"Windows OS is not configured in accordance with STIG rule SV-78287r1_rule!";
	}
	
	
}
