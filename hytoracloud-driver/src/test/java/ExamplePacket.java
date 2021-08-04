import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ExamplePacket extends Packet {

    private String name;
    private int age;

    @Override
    public void write(Component component) {
        component.put("name", name);
        component.put("age", age);
    }

    @Override
    public void read(Component component) {
        name = component.get("name");
        age = component.get("age");
    }
}
