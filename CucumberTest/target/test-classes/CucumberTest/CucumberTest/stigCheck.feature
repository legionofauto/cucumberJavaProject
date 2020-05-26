@tag
Feature: Quick STIG check
  This feature will perform a few STIG checks on you local Windows OS.

  @tag
  Scenario: LAN Manager hash config check

    When the Windows Local Security Authority registry information is retrieved
    Then Windows is configured to not store LAN Manager hash values

  @tag
  Scenario: Always install with elevated privileges must be disabled

    When the Windows Installer registry information is retrieved
    Then Windows Always install with elevated privileges config item is disabled
