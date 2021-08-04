package de.lystx.hytoracloud.driver.service.template;

import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;

public interface ITemplateManager {

    /**
     * Copies a service into a template
     *
     * @param service the service
     * @param template the template
     */
    void copyTemplate(IService service, ITemplate template);

    /**
     * Copies a server into a specific Template
     * but it only copies a specific folder like "world"
     * or the "plugins" folder or "plugins/YourFolder"
     *
     * @param service the service
     * @param template the template
     * @param specificDirectory a specific directory
     */
    void copyTemplate(IService service, ITemplate template, String specificDirectory);

    /**
     * Creates a Template for a group
     *
     * @param group the group
     * @param template the template
     */
    void createTemplate(IServiceGroup group, ITemplate template);

    /**
     * Creates a Template for the default template of a group
     *
     * @param serviceGroup the group
     */
    default void createTemplate(IServiceGroup serviceGroup) {
        this.createTemplate(serviceGroup, serviceGroup.getCurrentTemplate());
    }

    /**
     * Gets a template by name
     *
     * @param name the name
     * @param serviceGroup the group
     * @return template of group
     */
    ITemplate getTemplate(IServiceGroup serviceGroup, String name);
}
