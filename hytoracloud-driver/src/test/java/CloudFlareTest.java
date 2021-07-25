import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareAPI;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.manage.CloudFlareAuth;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareRequest;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareResponse;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.constants.CloudFlareAction;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.dns.DNSRecord;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.zone.Zone;

import java.util.UUID;

public class CloudFlareTest {


    public static void main(String[] args) {

        new CloudFlareAPI(new CloudFlareAuth("f9e99a4817d03f44ae42d08f39ffb4b0753d3", "wtfcxt@gmail.com"), "5dab5b08e0a4c182ba774b8cd2915ea0");

        System.out.println("[CloudFlareAPI] Booting up API...");

        System.out.println("[CloudFlareAPI] Loading Zones...");
        for (Zone zone : CloudFlareAPI.getInstance().getZones()) {
            System.out.println("[CloudFlareAPI] " + zone.getName() + " | " + zone.getId() + " | " + zone.getStatus());
        }
        System.out.println("-----");

        System.out.println("[CloudFlareAPI] Loading DNSRecords...");
        for (DNSRecord dnsRecord : CloudFlareAPI.getInstance().getDNSRecords()) {
            System.out.println("[CloudFlareAPI] " + dnsRecord.getName() + " | " + dnsRecord.getId() + " | " + dnsRecord.getContent());
        }
        System.out.println("-----");

    }
}
