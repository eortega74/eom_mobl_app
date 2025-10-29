package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.model;

import java.util.List;

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
* 
*/

public class TypeRegistratorService<T extends SelectOptionType> {

	private final List<T> memberTypes;
	
	public TypeRegistratorService( final List<T> memberTypes) {
		this.memberTypes=memberTypes;		
	}

	public T getMemberTypeFromString(final String typeString) throws InstantiationException {

        for(final T memberType : memberTypes) {
            final String matchingRegex = memberType.getMatchingRegex();
            // Make things case insensitive by wrapping it in (?i:%s)
            final String pattern = String.format("(?i:%s)", matchingRegex);
            if (typeString.matches(pattern)) {
                return memberType;
            }
        }

        throw new InstantiationException("Could not match the input string: " + typeString + " to a user class");
    }

    public String getCombinedTypeString() {
        // Make things case insensitive by wrapping it in (?i:%s)
        final StringBuilder combinedRegex = new StringBuilder("(?i:");

        boolean isFirstIteration = true;
        for(final T memberType : memberTypes) {
            final String regex = memberType.getMatchingRegex();
            if (isFirstIteration) {
                isFirstIteration = false;
            }
            else {
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
