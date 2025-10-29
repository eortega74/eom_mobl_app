package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.cucumber.step;

import static java.util.Locale.ENGLISH;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user.DriverUser;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user.UserTypeRegistratorService;
import com.rtx.eas.qas.it.test.seleniumlite.MasterProject.model.*;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.DataTableType;

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
* 
* A custom TypeRegistryConfigurer which enables custom types to be instantiated
* Currently includes custom types for DriverUsers, AgreementTypes, ITARAuthorization, and Triggers
* as well as a DataTable for P130 sweep determination.
*
* @author nrp0243799
*/

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    /**
     * a singleton user service which is instantiated against the user beans to provide regex/matching against them
     */
    private UserTypeRegistratorService userService;
    private ExampleTypeRegistratorService exampleService;
    
    /**
     * (non-Javadoc)
     * @see cucumber.api.TypeRegistryConfigurer#locale()
     **/
    @Override
    public Locale locale() {
        return ENGLISH;
    }

    /**
     * Retrieve the userService bean, allowing for lazy instantiation
     * also avoids trouble of worrying about how this class is actually instantiated and creating it there
     *
     * @return
     */
    private UserTypeRegistratorService getUserService() {
        if (userService == null) {
            synchronized(this) {
                if (userService == null) {
                    instantiateUserService();
                }
            }
        }
        return userService;
    }

    /**
     * Instantiates the user service bean for the class
     */
    private void instantiateUserService() {
        final AbstractApplicationContext userContext = new AnnotationConfigApplicationContext("com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user");
        try {
            userService = userContext.getBean(UserTypeRegistratorService.class);
        }
        catch(final BeansException e) {
        	System.out.println("Error occured while retrieving user type registrator bean.  "+ e.getMessage());
        }
        finally {
            userContext.close();
        }
    }
    
    /**
     * Retrieve the exampleService bean, allowing for lazy instantiation
     * also avoids trouble of worrying about how this class is actually instantiated and creating it there
     *
     * @return
     */
    private ExampleTypeRegistratorService getExampleService() {
        if (exampleService == null) {
            synchronized(this) {
                if (exampleService == null) {
                    instantiateExampleService();
                }
            }
        }
        return exampleService;
    }

    /**
     * Instantiates the example service bean for the class
     */
    private void instantiateExampleService() {
        final AbstractApplicationContext exampleContext = new AnnotationConfigApplicationContext("com.rtx.eas.qas.it.test.seleniumlite.MasterProject.model");
        try {
            exampleService = exampleContext.getBean(ExampleTypeRegistratorService.class);
        }
        catch(final BeansException e) {
        	System.out.println("Error occured while retrieving example type registrator bean.  "+ e.getMessage());
        }
        finally {
           exampleContext.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see cucumber.api.TypeRegistryConfigurer#configureTypeRegistry(cucumber.api.TypeRegistry)
     */
    @Override
    public void configureTypeRegistry(final TypeRegistry typeRegistry) {
		final UserTypeRegistratorService userService = this.getUserService();
		final ExampleTypeRegistratorService exampleService = this.getExampleService();
		
		typeRegistry.defineParameterType(new ParameterType<DriverUser>("user", userService.getCombinedTypeString(),
			        DriverUser.class, userService::getUserFromString));
		
		typeRegistry.defineParameterType(new ParameterType<ExampleType>("ITEM",
				exampleService.getCombinedTypeString(),ExampleType.class, exampleService::getMemberTypeFromString));
		typeRegistry.defineParameterType(new ParameterType<ExampleType>("URL",
				exampleService.getCombinedTypeString(),ExampleType.class, exampleService::getMemberTypeFromString));
	
		typeRegistry.defineDataTableType(new DataTableType(DriverUser.class, userService::getUserFromString));
		
		typeRegistry.defineDataTableType(new DataTableType(ExampleTableType.class,
				(Map<String, String> row) -> new ExampleTableType(
					row.get("ITEM"),
					row.get("URL")
					)));
    }
}
