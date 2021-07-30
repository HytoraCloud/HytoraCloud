package packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor @NoArgsConstructor
public class ExamplePacket extends Packet {

    private String name;
    private int age;

    @Override
    public void write(Component component) {
        component.append(map -> {
           map.put("name", name);
           map.put("age", age);
        });
    }

    @Override
    public void read(Component component) {
        name = component.get("name");
        age = component.get("age");
    }
}
