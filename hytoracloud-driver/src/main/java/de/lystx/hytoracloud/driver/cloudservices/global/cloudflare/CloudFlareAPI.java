package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.enums.CloudFlareAction;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.dns.DNSRecord;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user.CloudFlareUser;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user.UserOrganization;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone.CloudFlareZone;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CloudFlareAPI {

    /**
     * The login credentials
     */
    private final CloudFlareAuth auth;

    /**
     * The token
     */
    private final String token;

    @Getter
    private static CloudFlareAPI instance;

    public CloudFlareAPI(CloudFlareAuth auth, String token) {
        instance = this;

        this.auth = auth;
        this.token = token;
    }

    /**
     * ==========================
     *      User MANAGING
     * ==========================
     */

    /**
     * Gets a list of all {@link CloudFlareUser}s
     *
     * @return list of users
     */
    public List<CloudFlareUser> getUsers() {
        CloudFlareRequest<CloudFlareUser> flareRequest = new CloudFlareRequest<>(CloudFlareAction.USER_DETAILS, auth);
        return flareRequest.typeClass(CloudFlareUser.class).singleTonOrList().asList();
    }

    /**
     * Gets an {@link CloudFlareZone} by username
     *
     * @param username the username
     * @return the user or null
     */
    public CloudFlareUser getUser(String username) {
        return this.getUsers().stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    /**
     * Gets an {@link CloudFlareZone} by id
     *
     * @param id the username
     * @return the user or null
     */
    public CloudFlareUser getUserById(String id) {
        return this.getUsers().stream().filter(user -> user.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    /**
     * ==========================
     *      ZONE MANAGING
     * ==========================
     */

    /**
     * Gets an {@link CloudFlareZone} by name
     *
     * @param name the name
     * @return the zone or null
     */
    public CloudFlareZone getZone(String name) {
        return this.getZones().stream().filter(cloudFlareZone -> cloudFlareZone.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets an {@link CloudFlareZone} by id
     *
     * @param id the id
     * @return the zone or null
     */
    public CloudFlareZone getZoneById(String id) {
        return this.getZones().stream().filter(cloudFlareZone -> cloudFlareZone.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    /**
     * Gets a list of all {@link CloudFlareZone}s
     *
     * @return list zone
     */
    public List<CloudFlareZone> getZones() {
        CloudFlareResponse<CloudFlareZone> listCloudFlareResponse = new CloudFlareRequest<CloudFlareZone>(CloudFlareAction.LIST_ZONES, auth).typeClass(CloudFlareZone.class).singleTonOrList();
        return listCloudFlareResponse.asList();
    }

    /**
     * ==========================
     *        DNS RECORD MANAGING
     * ==========================
     */

    /**
     * Gets an {@link DNSRecord} by name
     *
     * @param name the name
     * @return the record or null
     */
    public DNSRecord getRecord(String name) {
        return this.getDNSRecords().stream().filter(dnsRecord -> dnsRecord.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets an {@link DNSRecord} by id
     *
     * @param id the id
     * @return the record or null
     */
    public DNSRecord getRecordById(String id) {
        return this.getDNSRecords().stream().filter(dnsRecord -> dnsRecord.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    /**
     * Gets a list of all {@link DNSRecord}s
     *
     * @return list of records
     */
    public List<DNSRecord> getDNSRecords() {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_DNS_RECORDS, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.token);
        return flareRequest.singleTonOrList().asList();
    }

    /**
     * Creates a new {@link DNSRecord}
     *
     * @param record the record
     */
    public void createRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.CREATE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.token);
        flareRequest.asVoid();
    }

    /**
     * Updates a {@link DNSRecord}
     *
     * @param record the record
     */
    public void updateRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.UPDATE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.token, record.getName());
        flareRequest.asVoid();
    }

    /**
     * Deletes an existing {@link DNSRecord}
     *
     * @param record the record
     */
    public void deleteRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.DELETE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.token, record.getId());
        flareRequest.asVoid();
    }

    /**
     * ==========================
     *      Other MANAGING
     * ==========================
     */


    public List<CloudFlareUser> get() {
        CloudFlareRequest<CloudFlareUser> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_USER_POOLS, auth);
        flareRequest.typeClass(CloudFlareUser.class);
        return flareRequest.singleTonOrList().asList();

    }

    /**
     * Gets an {@link UserOrganization} by name
     *
     * @param name the name
     * @return the organization or null
     */
    public UserOrganization getOrganization(String name) {
        return this.getOrganizations().stream().filter(organization -> organization.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets an {@link UserOrganization} by id
     *
     * @param id the id
     * @return the organization or null
     */
    public UserOrganization getOrganizationById(String id) {
        return this.getOrganizations().stream().filter(organization -> organization.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    /**
     * Gets all {@link CloudFlareUser}s out of a {@link UserOrganization}
     *
     * @param organization the organization
     * @return list of users
     */
    public List<CloudFlareUser> getUsers(UserOrganization organization) {
        CloudFlareRequest<CloudFlareUser> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_ORGANIZATION_MEMBERS, auth);
        flareRequest.identifiers(organization.getId());
        flareRequest.typeClass(CloudFlareUser.class);
        return flareRequest.singleTonOrList().asList();
    }

    /**
     * Gets a list of all {@link UserOrganization}s
     *
     * @return list of organizations
     */
    public List<UserOrganization> getOrganizations() {
        CloudFlareRequest<UserOrganization> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_USER_ORGANIZATIONS, auth);
        flareRequest.typeClass(UserOrganization.class);
        return flareRequest.singleTonOrList().asList();
    }
}
