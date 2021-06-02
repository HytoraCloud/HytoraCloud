package de.lystx.hytoracloud.bridge.proxy.events.service;

import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerServiceGroupUpdateEvent extends Event {

    private final ServiceGroup serviceGroup;

}
