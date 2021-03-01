package de.lystx.cloudapi.proxy.events.service;

import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerServiceUpdateEvent extends Event {

    private final Service service;

}
