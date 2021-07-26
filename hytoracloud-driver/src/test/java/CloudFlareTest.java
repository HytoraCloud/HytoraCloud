import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.CloudFlareAPI;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareAuth;

public class CloudFlareTest {


    public static void main(String[] args) {

        new CloudFlareAPI(new CloudFlareAuth("f9e99a4817d03f44ae42d08f39ffb4b0753d3", "wtfcxt@gmail.com"), "5dab5b08e0a4c182ba774b8cd2915ea0");

    }
}
