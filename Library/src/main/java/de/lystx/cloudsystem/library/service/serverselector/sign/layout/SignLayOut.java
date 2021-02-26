package de.lystx.cloudsystem.library.service.serverselector.sign.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;

public class SignLayOut {

    private final VsonObject document;
    private final int repeatTick;

    public SignLayOut() {
        this(null);
    }

    public SignLayOut(VsonObject document) {
        this.document = document;
        this.repeatTick = 20;
    }

    public int getAnimationTick(){
        return this.getOfflineLayOut().size();
    }
    
    public VsonObject check() {
        return this.document == null ? new DefaultSignLayout() : this.document;
    }

    public int getRepeatTick() {
        return this.check().getInteger("repeatTick", this.repeatTick);
    }

    public VsonObject getOnlineLayOut() {
        return this.check().getVson("otherLayouts").getVson("ONLINE");
    }

    public VsonObject getMaintenanceLayOut() {
        return this.check().getVson("otherLayouts").getVson("MAINTENANCE");
    }

    public VsonObject getFullLayOut() {
        return this.check().getVson("otherLayouts").getVson("FULL");
    }

    public VsonArray getOfflineLayOut(){
        return this.check().getArray("loadingLayout");
    }

    public VsonObject getDocument() {
        return document;
    }
}
