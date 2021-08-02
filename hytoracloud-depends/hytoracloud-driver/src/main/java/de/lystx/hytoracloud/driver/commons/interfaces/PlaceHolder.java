package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
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
                    if (service != null) {
                        input = input.replace("%service%", service.getName());
                        input = input.replace("%server%", service.getName());
                        input = input.replace("%id%", service.getId() + "");
                        input = input.replace("%state%", service.getState().name());
                        input = input.replace("%port%", service.getPort() + "");
                        input = input.replace("%max_players%", service.getMaxPlayers() + "");
                        input = input.replace("%online_players%", service.getPlayers().size() + "");
                        input = input.replace("%max%", service.getMaxPlayers() + "");
                        input = input.replace("%online%", service.getPlayers().size() + "");
                        input = input.replace("%motd%", service.getMotd() == null ? "No_motd_found" : service.getMotd());
                        input = input.replace("%group%", service.getGroup().getName());
                    }

                    return input;
                }

                @Override
                public String applyIf(IService service, String input) {
                    input = input.replace("%proxy%", service.getName());
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
                public String applyIf(IServiceGroup group, String input) {
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
                public String applyIf(NetworkConfig group, String input) {
                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{NetworkConfig.class};
                }
            },
            new PlaceHolder<ICloudPlayer>() {
                @Override
                public String apply(ICloudPlayer player, String input) {
                    input = input.replace("%player%", player.getName());
                    input = input.replace("%uuid%", player.getUniqueId().toString());
                    input = input.replace("%ip%", player.getIpAddress());
                    input = input.replace("%rank", player.getCachedPermissionGroup().getName());
                    input = input.replace("%rank_color", player.getCachedPermissionGroup().getDisplay());
                    return input;
                }

                @Override
                public String applyIf(ICloudPlayer player, String input) {
                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{PlayerObject.class, ICloudPlayer.class};
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
                public String applyIf(PermissionGroup permissionGroup, String input) {
                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{PermissionGroup.class};
                }
            },
            new PlaceHolder<Motd>() {

                @Override
                public String apply(Motd motd, String input) {

                    input = input.replace("%max_players%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().getMaxPlayers()));
                    input = input.replace("%online_players%", String.valueOf(CloudDriver.getInstance().getPlayerManager().getCachedObjects().size()));
                    input = input.replace("%proxy%", CloudDriver.getInstance().getServiceManager().getThisService().getName());

                    return input;
                }

                @Override
                public String applyIf(Motd motd, String input) {
                    return input;
                }

                @Override
                public Class<?>[] getAcceptedClasses() {
                    return new Class[]{Motd.class};
                }
            }
    );

    @SafeVarargs
    static <V> String applyIf(String input, Requestable<V> requestable, V... objects) {
        for (PlaceHolder<?> placeHolder : PLACE_HOLDERS) {
            for (V object : objects) {
                for (Class<?> acceptedClass : placeHolder.getAcceptedClasses()) {
                    if (acceptedClass == null || object == null || object.getClass() == null) {
                        continue;
                    }
                    if (acceptedClass.equals(object.getClass())) {
                        PlaceHolder<V> vPlaceHolder = (PlaceHolder<V>)placeHolder;
                        if (requestable.isRequested(object)) {
                            input = vPlaceHolder.applyIf(object, input);
                        } else {
                            input = vPlaceHolder.apply(object, input);
                        }
                    }
                }
            }
        }
        return input.replace("&", "§");
    }
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

        return input.replace("%prefix%", CloudDriver.getInstance().getPrefix()).replace("&", "§");
    }

    /**
     * Applies the placeholder
     *
     * @param v the object
     * @param input the input string
     */
    String apply(V v, String input);

    /**
     * Applies if something is true
     *
     * @param v the object
     * @param input the input
     * @return string input
     */
    String applyIf(V v, String input);

    Class<?>[] getAcceptedClasses();
}
