package de.lystx.hytoracloud.driver.service.group;

import de.lystx.hytoracloud.driver.utils.interfaces.ObjectPool;

public interface IGroupManager extends ObjectPool<IServiceGroup> {

    /**
     * Updates a {@link IServiceGroup}
     *
     * @param group the old group
     */
    void update(IServiceGroup group);

    /**
     * Creates a new {@link IServiceGroup}
     *
     * @param serviceGroup the group object
     */
    void createGroup(IServiceGroup serviceGroup);

    /**
     * Deletes an existing {@link IServiceGroup}
     *
     * @param serviceGroup the group object
     */
    void deleteGroup(IServiceGroup serviceGroup);
}
