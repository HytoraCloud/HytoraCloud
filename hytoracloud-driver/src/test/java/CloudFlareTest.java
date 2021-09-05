import de.lystx.hytoracloud.driver.utils.json.JsonData;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;

public class CloudFlareTest {


    public static void main(String[] args) {

        long l = System.currentTimeMillis();
        JsonData jsonData = new JsonData();
        System.out.println(System.currentTimeMillis() - l + " created");
        l = System.currentTimeMillis();
        System.out.println(jsonData.fallback("LOL").getString("PENIS"));
        System.out.println(System.currentTimeMillis() - l);

    }
}
