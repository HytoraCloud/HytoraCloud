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

        JsonObject<?> jsonObject = JsonObject.gson((String) component.get("event"));
        Class<?> eventClass = Class.forName(component.get("class"));

        cloudEvent = (CloudEvent) jsonObject.getAs(eventClass);
        this.except = component.get("except");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        JsonObject<?> jsonObject = JsonObject.gson().append(cloudEvent);

        component.put("event", jsonObject.toString());
        component.put("class", cloudEvent.getClass().getName());
        component.put("except", except);
    }

}
