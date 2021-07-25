package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.constants.CloudFlareAction;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.manage.CloudFlareAuth;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.dns.DNSRecord;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.zone.Zone;
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
     *      ZONE MANAGING
     * ==========================
     */


    /**
     * Gets an {@link Zone} by name
     *
     * @param name the name
     * @return the zone or null
     */
    public Zone getZone(String name) {
        return this.getZones().stream().filter(zone -> zone.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets an {@link Zone} by uniqueId
     *
     * @param uniqueId the name
     * @return the zone or null
     */
    public Zone getZone(UUID uniqueId) {
        return this.getZones().stream().filter(zone -> zone.getId().equalsIgnoreCase(uniqueId.toString())).findFirst().orElse(null);
    }

    /**
     * Gets a list of all {@link Zone}s
     *
     * @return list zone
     */
    public List<Zone> getZones() {
        CloudFlareResponse<List<Zone>> listCloudFlareResponse = new CloudFlareRequest(CloudFlareAction.LIST_ZONES, auth).asList(Zone.class);
        return listCloudFlareResponse.getObject();
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
        CloudFlareRequest flareRequest = new CloudFlareRequest(CloudFlareAction.LIST_DNS_RECORDS, this.auth);
        flareRequest.identifiers(this.token);
        return flareRequest.asCollection(DNSRecord.class).asList();
    }

    /**
     * Creates a new {@link DNSRecord}
     *
     * @param record the record
     */
    public void createRecord(DNSRecord record) {
        CloudFlareRequest flareRequest = new CloudFlareRequest(CloudFlareAction.CREATE_DNS_RECORD, this.auth);
        flareRequest.identifiers(this.token);
        flareRequest.asVoid();
    }

    /**
     * Updates a {@link DNSRecord}
     *
     * @param record the record
     */
    public void updateRecord(DNSRecord record) {
        CloudFlareRequest flareRequest = new CloudFlareRequest(CloudFlareAction.UPDATE_DNS_RECORD, this.auth);
        flareRequest.identifiers(this.token, record.getName());
        flareRequest.asVoid();
    }

    /**
     * Deletes an existing {@link DNSRecord}
     *
     * @param record the record
     */
    public void deleteRecord(DNSRecord record) {
        CloudFlareRequest flareRequest = new CloudFlareRequest(CloudFlareAction.DELETE_DNS_RECORD, this.auth);
        flareRequest.identifiers(this.token, record.getId());
        flareRequest.asVoid();
    }
}
