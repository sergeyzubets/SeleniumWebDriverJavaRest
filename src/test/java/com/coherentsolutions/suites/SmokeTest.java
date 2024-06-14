package com.coherentsolutions.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@IncludeTags("smoke")
@SelectPackages({"com.coherentsolutions.zipCodeClient", "com.coherentsolutions.userClient"})
@SuiteDisplayName("Smoke Test Suite")
public class SmokeTest {
}