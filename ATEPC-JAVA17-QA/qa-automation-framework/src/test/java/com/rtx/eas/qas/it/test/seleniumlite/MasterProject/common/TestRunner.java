package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.common;

/**
* =============================================================================
*                           RAYTHEON PROPRIETARY
*
* Company Name: Raytheon Company
* Company Address: 870 Winter Street Waltham, MA 02451-1449
*
* Unpublished work Copyright 2016 Raytheon Company.
*
* Contact Methods: EAS-QAS
*
* This document contains proprietary data or information pertaining to items,
* or components, or processes, or other matter developed or acquired at the
* private expense of the Raytheon Company and is restricted to use only by
* persons authorized by Raytheon in writing to use it. Disclosure to
* unauthorized persons would likely cause substantial competitive harm to
* Raytheon's business position. Neither said document nor said technical data or
* information shall be furnished or disclosed to or copied or used by persons
* outside Raytheon without the express written approval of Raytheon.
*
* This Proprietary Notice Is Not Applicable If Delivered To The US Government
*
* =============================================================================
* @author nrp0236009/40003339
*
* Change Log:
* 05/11/2022 nrp0253353
* Removed all Extent Report-related code
* -----------------------------------------------------------------------------
*/

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.selenium.BaseConfiguration;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(	features="classpath:features", 
				 	glue="com/rtx/eas/qas/it/test/seleniumlite/MasterProject/cucumber/step",
				 	tags="@Example1",
				 	plugin = {"pretty", "html:target/Reports"},
				 	dryRun = false,
				 	monochrome = false,
				 	strict = true
				 )

public class TestRunner {
	
	static BaseConfiguration baseConf = new BaseConfiguration();
	
	public static void main(String[] args) {
		JUnitCore.main(TestRunner.class.getName());
	}
	
	@BeforeClass
	public static void createReportFolder() throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy_hh_mm_ss");
		String date = simpleDateFormat.format(new Date()).toString();
		new File(System.getProperty("user.dir") + "/Screenshots/").mkdir();
		if(baseConf.getExecutionLocation().equalsIgnoreCase("local")) {
			File file = new File(System.getProperty("user.dir") + "/Screenshots/" + date);
			file.mkdir();
			}
	}
	@AfterClass
	public static void publishReports() {
		
				System.out.println("test after");
							
	}

}