package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.other;

import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import lombok.SneakyThrows;

@Getter @AllArgsConstructor
public class PacketCallEvent extends PacketCommunication {

    /**
     * If events should be parsed as json string
     */
    private static final boolean USE_JSON = false;

    /**
     * The cloud event
     */
    private IEvent iEvent;

    /**
     * Who should not receive the event
     */
    private String except;


    @Override @SneakyThrows
    public void read(Component component) {
        super.read(component);

        if (USE_JSON) {
            JsonObject<?> jsonObject = JsonObject.gson((String) component.get("event"));
            Class<?> eventClass = Class.forName(component.get("class"));

            this.iEvent = (IEvent) jsonObject.getAs(eventClass);
        } else {
            this.iEvent = component.get("event");
        }
        this.except = component.get("except");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        if (USE_JSON) {
            JsonObject<?> jsonObject = JsonObject.gson().append(iEvent);
            component.put("class", iEvent.getClass().getName());
            component.put("event", jsonObject.toString());
        } else {
            component.put("event", iEvent);
        }
        component.put("except", except);
    }

}
