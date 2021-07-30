package de.lystx.hytoracloud.driver.commons.packets.both.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
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
    private CloudEvent cloudEvent;

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

            this.cloudEvent = (CloudEvent) jsonObject.getAs(eventClass);
        } else {
            this.cloudEvent = component.get("event");
        }
        this.except = component.get("except");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        if (USE_JSON) {
            JsonObject<?> jsonObject = JsonObject.gson().append(cloudEvent);
            component.put("class", cloudEvent.getClass().getName());
            component.put("event", jsonObject.toString());
        } else {
            component.put("event", cloudEvent);
        }
        component.put("except", except);
    }

}
