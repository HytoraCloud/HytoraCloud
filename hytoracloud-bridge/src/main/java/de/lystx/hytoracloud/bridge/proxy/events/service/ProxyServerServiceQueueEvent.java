package de.lystx.hytoracloud.bridge.proxy.events.service;

import de.lystx.hytoracloud.driver.elements.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerServiceQueueEvent extends Event {

    private final Service service;

}
