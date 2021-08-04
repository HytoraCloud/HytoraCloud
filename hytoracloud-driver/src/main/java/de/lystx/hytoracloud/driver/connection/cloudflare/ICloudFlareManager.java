package de.lystx.hytoracloud.driver.connection.cloudflare;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.dns.DNSRecord;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.CloudFlareUser;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.UserOrganization;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.zone.CloudFlareZone;

import java.util.List;

public interface ICloudFlareManager {

    /**
     * Loads the api with a given auth
     *
     * @param auth the auth login data
     */
    void setAuth(CloudFlareAuth auth);

    /**
     * Sets the zoneId for dns managing
     *
     * @param zoneId the id
     */
    void setZoneId(String zoneId);


    /**
     * Gets a list of all {@link CloudFlareUser}s
     *
     * @return list of users
     */
    List<CloudFlareUser> getUsers();

    /**
     * Gets an {@link CloudFlareZone} by username
     *
     * @param username the username
     * @return the user or null
     */
    CloudFlareUser getUser(String username);

    /**
     * Gets an {@link CloudFlareZone} by id
     *
     * @param id the username
     * @return the user or null
     */
    CloudFlareUser getUserById(String id);

    /**
     * Gets an {@link CloudFlareZone} by name
     *
     * @param name the name
     * @return the zone or null
     */
    CloudFlareZone getZone(String name);

    /**
     * Gets an {@link CloudFlareZone} by id
     *
     * @param id the id
     * @return the zone or null
     */
    CloudFlareZone getZoneById(String id);

    /**
     * Gets a list of all {@link CloudFlareZone}s
     *
     * @return list zone
     */
    List<CloudFlareZone> getZones();

    /**
     * Gets an {@link UserOrganization} by name
     *
     * @param name the name
     * @return the organization or null
     */
    UserOrganization getOrganization(String name);

    /**
     * Gets an {@link UserOrganization} by id
     *
     * @param id the id
     * @return the organization or null
     */
    UserOrganization getOrganizationById(String id);

    /**
     * Gets a list of all {@link UserOrganization}s
     *
     * @return list of organizations
     */
    List<UserOrganization> getOrganizations();


    /**
     * Deletes an existing {@link DNSRecord}
     *
     * @param record the record
     */
    void deleteRecord(DNSRecord record);

    /**
     * Updates an existing {@link DNSRecord}
     *
     * @param record the record
     */
    void updateRecord(DNSRecord record);

    /**
     * Creates a new {@link DNSRecord}
     *
     * @param record the record
     */
    void createRecord(DNSRecord record);

    /**
     * Gets a list of all {@link DNSRecord}s
     *
     * @return list of records
     */
    List<DNSRecord> getDNSRecords();


    /**
     * Gets an {@link DNSRecord} by name
     *
     * @param name the name
     * @return the record or null
     */
    DNSRecord getRecord(String name);

    /**
     * Gets an {@link DNSRecord} by id
     *
     * @param id the id
     * @return the record or null
     */
    DNSRecord getRecordById(String id);
}
