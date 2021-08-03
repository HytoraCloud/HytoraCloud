package de.lystx.hytoracloud.driver.connection.cloudflare.def;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.query.CloudFlareRequest;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.query.CloudFlareResponse;
import de.lystx.hytoracloud.driver.connection.cloudflare.ICloudFlareManager;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.enums.CloudFlareAction;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.dns.DNSRecord;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.CloudFlareUser;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.UserOrganization;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.zone.CloudFlareZone;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DefaultCloudFlareManager implements ICloudFlareManager {

    private CloudFlareAuth auth; //The login credentials
    private String zoneId; //The zone id

    @Override
    public List<CloudFlareUser> getUsers() {
        CloudFlareRequest<CloudFlareUser> flareRequest = new CloudFlareRequest<>(CloudFlareAction.USER_DETAILS, auth);
        return flareRequest.typeClass(CloudFlareUser.class).singleTonOrList().asList();
    }

    @Override
    public CloudFlareUser getUser(String username) {
        return this.getUsers().stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    @Override
    public CloudFlareUser getUserById(String id) {
        return this.getUsers().stream().filter(user -> user.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public CloudFlareZone getZone(String name) {
        return this.getZones().stream().filter(cloudFlareZone -> cloudFlareZone.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public CloudFlareZone getZoneById(String id) {
        return this.getZones().stream().filter(cloudFlareZone -> cloudFlareZone.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public List<CloudFlareZone> getZones() {
        CloudFlareResponse<CloudFlareZone> listCloudFlareResponse = new CloudFlareRequest<CloudFlareZone>(CloudFlareAction.LIST_ZONES, auth).typeClass(CloudFlareZone.class).singleTonOrList();
        return listCloudFlareResponse.asList();
    }

    @Override
    public DNSRecord getRecord(String name) {
        return this.getDNSRecords().stream().filter(dnsRecord -> dnsRecord.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public DNSRecord getRecordById(String id) {
        return this.getDNSRecords().stream().filter(dnsRecord -> dnsRecord.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public List<DNSRecord> getDNSRecords() {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_DNS_RECORDS, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.zoneId);
        return flareRequest.singleTonOrList().asList();
    }

    @Override
    public void createRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.CREATE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.zoneId);
        flareRequest.asVoid();
    }

    @Override
    public void updateRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.UPDATE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.zoneId, record.getName());
        flareRequest.asVoid();
    }

    @Override
    public void deleteRecord(DNSRecord record) {
        CloudFlareRequest<DNSRecord> flareRequest = new CloudFlareRequest<>(CloudFlareAction.DELETE_DNS_RECORD, this.auth);
        flareRequest.typeClass(DNSRecord.class);
        flareRequest.identifiers(this.zoneId, record.getId());
        flareRequest.asVoid();
    }

    @Override
    public UserOrganization getOrganization(String name) {
        return this.getOrganizations().stream().filter(organization -> organization.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public UserOrganization getOrganizationById(String id) {
        return this.getOrganizations().stream().filter(organization -> organization.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public List<UserOrganization> getOrganizations() {
        CloudFlareRequest<UserOrganization> flareRequest = new CloudFlareRequest<>(CloudFlareAction.LIST_USER_ORGANIZATIONS, auth);
        flareRequest.typeClass(UserOrganization.class);
        return flareRequest.singleTonOrList().asList();
    }

}
