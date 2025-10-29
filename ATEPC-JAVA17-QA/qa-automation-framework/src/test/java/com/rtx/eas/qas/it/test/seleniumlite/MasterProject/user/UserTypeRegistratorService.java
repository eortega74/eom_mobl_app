package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
*Change Log:
*
*/

/**
 * User type registrator used to automatically add user types to the type registry
 *
 * @author nrp0243799
 */
@Service
public class UserTypeRegistratorService
{

    /**
     * A list of the driver user beans that this manages
     */
    private final List<DriverUser> driverUsers;

    /**
     * @param driverUsers
     */
    @Autowired
    public UserTypeRegistratorService(final List<DriverUser> driverUsers)
    {
        this.driverUsers = driverUsers;
    }

    /**
     * Returns a user from an input string to use with cucumber
     *
     * @param userString
     * @return
     * @throws InstantiationException
     */
    public DriverUser getUserFromString(final String userString) throws InstantiationException
    {
    	System.out.println("Retrieving user for " + userString);

        // Iterate over the user map entries
        // See if the user string matches any of the user patterns
        for(final DriverUser user : driverUsers)
        {
            final String matchingRegex = user.getMatchingRegex();
            // Make things case insensitive by wrapping it in (?i:%s)
            final String pattern = String.format("(?i:%s)", matchingRegex);
            if (userString.matches(pattern))
            {
                return user;
            }
        }

        throw new InstantiationException("Could not match the input string: " + userString + " to a user class");
    }

    /**
     * Combine all of the regex's for each of the types into a single regex for cucumber to match against
     *
     * @return
     */
    public String getCombinedTypeString()
    {
        // Make things case insensitive by wrapping it in (?i:%s)
        final StringBuilder combinedRegex = new StringBuilder("(?i:");

        boolean isFirstIteration = true;
        for(final DriverUser user : driverUsers)
        {
            final String regex = user.getMatchingRegex();
            if (isFirstIteration)
            {
                isFirstIteration = false;
            }
            else
            {
                combinedRegex.append('|');
            }
            combinedRegex.append('(');
            combinedRegex.append(regex);
            combinedRegex.append(')');
        }
        combinedRegex.append(')');
        System.out.println(combinedRegex.toString());

        return combinedRegex.toString();
    }
}
