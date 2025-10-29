package com.rtx.eas.qas.it.test.seleniumlite.MasterProject.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 * Spring configuration for the cucumber user classes
 *
 * All the automated test user accounts should be identified in this file.  
 * 
 * The format is username,  role, full name, and the regular expression.
 * The regular expression matched against the cucumber feature file to identify the specified. user.
 * 
 * Example:
 * 
 * User nrponeide001 has the role of BU AO for IDS
 * Their regular expression is "nrponeide001|P(ART)?( )?130 AO IDS"
 * that regular expression will be treated as case insensitive when matching.
 * 
 * nrponeide001 can be identified in a feature file as 
 * of the following:
 * NRPONEIDE001
 * PART 130 AO IDS
 * PART130 AO IDS
 * P130 AO IDS
 * P 130 AO IDS	
 *
 *
 * This class is annotated as Configuration, which means that the annotated beans (the DriverUser)
 * will be available for autowiring (see Autowired annotation).  
 * This is used in UsertTypeRegistratorService to collect these into a single list.
 * 
 * You will find this pattern used for all the various *TypeConfiguration classes
 * @see com.rtx.eas.qas.it.test.seleniumlite.MasterProject.model.TypeRegistratorService for the generic applied to UI input fields.
 * 
 * This 
 * @author nrp0243799
 */
@Configuration
public class UserConfiguration
{

	
    /**
     * nrponeide001
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide001() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide001", "nrponeide001", "nrponeide001", "nrponeide001");
    }

    /**
     * nrponeide002
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide002() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide002", "nrponeide002", "nrponeide002", "nrponeide002");
    }
    /**
     * nrponeide003
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide003() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide003", "nrponeide003", "nrponeide003", "nrponeide003");
    }
    /**
     * nrponeide004
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide004() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide004", "nrponeide004", "nrponeide004", "nrponeide004");
    }
    /**
     * nrponeide005
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide005() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide005", "nrponeide005", "nrponeide005", "nrponeide005");
    }
    /**
     * nrponeide006
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide006() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide006", "nrponeide006", "nrponeide006", "nrponeide006");
    }
    /**
     * nrponeide007
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide007() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide007", "nrponeide007", "nrponeide007", "nrponeide007");
    }
    /**
     * nrponeide008
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide008() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide008", "nrponeide008", "nrponeide008", "nrponeide008");
    }
    /**
     * nrponeide009
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide009() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide009", "nrponeide009", "nrponeide009", "nrponeide009");
    }
    /**
     * nrponeide010
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide010() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide010", "nrponeide010", "nrponeide010", "nrponeide010");
    }
    /**
     * nrponeide011
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide011() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide011", "nrponeide011", "nrponeide011", "nrponeide011");
    }
    /**
     * nrponeide012
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide012() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide012", "nrponeide012", "nrponeide012", "nrponeide012");
    }
    /**
     * nrponeide013
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide013() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide013", "nrponeide013", "nrponeide013", "nrponeide013");
    }
    /**
     * nrponeide015
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser nrponeide015() throws InstantiationException
    {
        return new SimpleDriverUser("nrponeide015", "nrponeide015", "nrponeide015", "nrponeide015");
    }

	/**
     * oneide020
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide020() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide020", "103oneide020", "103oneide020", "103oneide020");
    }
    /**
     * oneide021
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide021() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide021", "103oneide021", "103oneide021", "103oneide021");
    }
    /**
     * oneide022
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide022() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide022", "103oneide022", "103oneide022", "103oneide022");
    }
    /**
     * oneide023
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide023() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide023", "103oneide023", "103oneide023", "103oneide023");
    }
    /**
     * oneide024
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide024() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide024", "103oneide024", "103oneide024", "103oneide024");
    }
    /**
     * oneide025
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide025() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide025", "103oneide025", "103oneide025", "103oneide025");
    }
    /**
     * oneide026
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide026() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide026", "103oneide026", "103oneide026", "103oneide026");
    }
    /**
     * oneide027
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide027() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide027", "103oneide027", "103oneide027", "103oneide027");
    }
    /**
     * oneide028
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide028() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide028", "103oneide028", "103oneide028", "103oneide028");
    }
    /**
     * oneide029
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide029() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide029", "103oneide029", "103oneide029", "103oneide029");
    }
    /**
     * oneide030
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide030() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide030", "103oneide030", "103oneide030", "103oneide030");
    }
    /**
     * oneide031
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide031() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide031", "103oneide031", "103oneide031", "103oneide031");
    }
    /**
     * oneide032
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide032() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide032", "103oneide032", "103oneide032", "103oneide032");
    }
    /**
     * oneide033
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser oneide033() throws InstantiationException
    {
        return new SimpleDriverUser("103oneide033", "103oneide033", "103oneide033", "103oneide033");
    }
    /**
     * 103sp22
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser sp22() throws InstantiationException
    {
        return new SimpleDriverUser("103sp22", "103sp22", "103sp22", "103sp22");
    }
    /**
     * 103sp23
     *
     * @return
     * @throws InstantiationException
     */
    @Bean
    public DriverUser sp23() throws InstantiationException
    {
        return new SimpleDriverUser("103sp23", "103sp23", "103sp23", "103sp23");
    }
    
}
