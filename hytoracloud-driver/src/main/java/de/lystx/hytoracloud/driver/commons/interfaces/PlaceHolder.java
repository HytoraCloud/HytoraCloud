package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;

import java.util.Arrays;
import java.util.List;

public interface PlaceHolder<V> {

    List<PlaceHolder<?>> PLACE_HOLDERS = Arrays.asList(

            //For services
            new PlaceHolder<IService>() {
                @Override
                public String apply(IService service, String input) {
                    input = input.replace("%service%", service.getName());
                    input = input.replace("%server%", service.getName());
                    input = input.replace("%proxy%", service.getName());
                    input = input.replace("%id%", service.getId() + "");
                    input = input.replace("%state%", service.getState().name());
                    input = input.replace("%port%", service.getPort() + "");
                    input = input.replace("%max_players%", service.getMaxPlayers() + "");
                    input = input.replace("%online_players%", service.getPlayers().size() + "");
                    input = input.replace("%max%", service.getMaxPlayers() + "");
                    input = input.replace("%online%", service.getPlayers().size() + "");
                    input = input.replace("%motd%", service.getMotd());
                    input = input.replace("%group%", service.getGroup().getName());

                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{IService.class, ServiceObject.class};
                }
            },

            //For servicegroups
            new PlaceHolder<IServiceGroup>() {
                @Override
                public String apply(IServiceGroup group, String input) {
                    input = input.replace("%group%", group.getName());
                    input = input.replace("%template%", group.getCurrentTemplate().getName());
                    input = input.replace("%receiver%", group.getReceiver());
                    input = input.replace("%wrapper%", group.getReceiver());
                    input = input.replace("%max_players%", group.getMaxPlayers() + "");
                    input = input.replace("%online_players%", group.getPlayers().size() + "");
                    input = input.replace("%services%", group.getServices().size() + "");
                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{IServiceGroup.class, ServiceGroupObject.class};
                }
            },

            //For network config,
            new PlaceHolder<NetworkConfig>() {
                @Override
                public String apply(NetworkConfig networkConfig, String input) {
                    input = input.replace("%prefix%", networkConfig.getMessageConfig().getPrefix());

                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{NetworkConfig.class};
                }
            },

            //For perms groups
            new PlaceHolder<PermissionGroup>() {
                @Override
                public String apply(PermissionGroup permissionGroup, String input) {

                    input = input.replace("%permission_group%", permissionGroup.getName());
                    input = input.replace("%prefix%", permissionGroup.getPrefix());
                    input = input.replace("%suffix%", permissionGroup.getSuffix());
                    input = input.replace("%display%", permissionGroup.getDisplay());
                    input = input.replace("%group_id%", permissionGroup.getId() + "");

                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{PermissionGroup.class};
                }
            }
    );

    /**
     * Applies all placeholers to a given string
     * with a given amount of input objects
     *
     * @param input the raw string
     * @param objects the objects
     * @param <V> the generic
     * @return formatted String
     */
    @SafeVarargs
    static <V> String apply(String input, V... objects) {
        for (PlaceHolder<?> placeHolder : PLACE_HOLDERS) {
            for (V object : objects) {
                for (Class<?> acceptedClass : placeHolder.getAcceptedClasses()) {
                    if (acceptedClass.equals(object.getClass())) {
                        PlaceHolder<V> vPlaceHolder = (PlaceHolder<V>)placeHolder;
                        input = vPlaceHolder.apply(object, input);
                    }
                }
            }
        }
        return input.replace("&", "§");
    }

    /**
     * Applies the placeholder
     *
     * @param v the object
     * @param input the input string
     */
    String apply(V v, String input);

    Class<?>[] getAcceptedClasses();
}
