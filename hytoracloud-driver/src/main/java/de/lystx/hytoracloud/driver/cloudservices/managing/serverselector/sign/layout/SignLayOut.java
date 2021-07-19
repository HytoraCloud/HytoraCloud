package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout;

import com.google.gson.JsonArray;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import utillity.JsonEntity;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

/**
 * This class manages the appearance of CloudSigns
 * Here the speed of the cloudsigns will be requested,
 * the Design of the CloudSign for a certain {@link ServiceState}
 * or the whole Loading-LayOut
 */
@Getter
public class SignLayOut {

    private final JsonEntity document;
    private final int repeatTick;

    public SignLayOut() {
        this(null);
    }

    /**
     * This creates the SignLayout mostly
     * with the {@link DefaultSignLayout} and sets
     * the repeatTick to default 20 (1 Seconds)
     * @param document
     */
    public SignLayOut(JsonEntity document) {
        this.document = document;
        this.repeatTick = 20;
    }

    /**
     * Defines the speed of updating
     * the CloudSigns (20 ticks = 1 Second)
     *
     * @return Tick for Updating of CloudSigns
     */
    public int getAnimationTick(){
        return this.getOfflineLayOut().size();
    }

    /**
     * Checks if SignLayout is null
     * then uses {@link DefaultSignLayout}
     * @return SignLayout parsed as {@link VsonObject}
     */
    public JsonEntity check() {
        return this.document == null ? new DefaultSignLayout() : this.document;
    }

    /**
     * Returns the repeat tick for the CloudSigns
     * to repeat (like a delay)
     * @return
     */
    public int getRepeatTick() {
        return this.check().getInteger("repeatTick", this.repeatTick);
    }

    /**
     * Returns the SignLayOut for
     * Services which are ONLINE
     * @return SignLayout as VsonObject
     */
    public JsonEntity getOnlineLayOut() {
        return this.check().getJson("otherLayouts").getJson("ONLINE");
    }

    /**
     * Returns the SignLayOut for
     * Services which are in MAINTENANCE
     * @return SignLayout as VsonObject
     */
    public JsonEntity getMaintenanceLayOut() {
        return this.check().getJson("otherLayouts").getJson("MAINTENANCE");
    }

    /**
     * Returns the SignLayOut for
     * Services which are FULL
     * @return SignLayout as VsonObject
     */
    public JsonEntity getFullLayOut() {
        return this.check().getJson("otherLayouts").getJson("FULL");
    }

    /**
     * Returns a custom SignLayout you choose
     * by name from the config
     * @return SignLayout as VsonObject
     */
    public JsonEntity getCustom(String name) {
        return this.check().getJson("otherLayouts").getJson(name);
    }

    /**
     * Returns the SignLayOut for
     * Services which are OFFLINE
     * @return SignLayout as VsonArray
     */
    public JsonArray getOfflineLayOut(){
        return this.check().getArray("loadingLayout");
    }

}
