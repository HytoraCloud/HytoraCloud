package de.lystx.cloudsystem.receiver.temp.template;

import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.receiver.Receiver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TemplateTemp {

    private final Receiver receiver;
    private final Map<ServiceGroup, File> templates;

    public TemplateTemp(Receiver receiver) {
        this.receiver = receiver;
        this.templates = new HashMap<>();
    }


    public void setTemplate(ServiceGroup serviceGroup, File file) {
        this.templates.put(serviceGroup, file);
    }

    public File getTemplate(ServiceGroup serviceGroup) {
        ServiceGroup group = this.templates.keySet().stream().filter(serviceGroup1 -> serviceGroup.getName().equalsIgnoreCase(serviceGroup1.getName())).findFirst().orElse(null);
        return this.templates.get(group);
    }
}
