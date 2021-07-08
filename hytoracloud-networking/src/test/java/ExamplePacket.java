import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor @NoArgsConstructor
public class ExamplePacket extends HytoraPacket {

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
        name = component.getString("name");
        age = component.getInteger("age");
    }
}
