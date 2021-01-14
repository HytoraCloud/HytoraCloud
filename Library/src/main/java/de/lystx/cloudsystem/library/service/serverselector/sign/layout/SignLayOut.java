package de.lystx.cloudsystem.library.service.serverselector.sign.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.cloudsystem.library.elements.other.Document;

public class SignLayOut {

    private final Document document;
    private final int repeatTick;

    public SignLayOut() {
        this(null);
    }

    public SignLayOut(Document document) {
        this.document = document;
        this.repeatTick = 20;
    }

    public int getAnimationTick(){
        return this.getOfflineLayOut().size();
    }
    
    public Document check() {
        return this.document == null ? new DefaultSignLayout() : this.document;
    }

    public int getRepeatTick() {
        return this.check().getInteger("repeatTick", this.repeatTick);
    }

    public JsonObject getOnlineLayOut() {
        return this.check().getDocument("otherLayouts").getJsonObject("ONLINE");
    }

    public JsonObject getMaintenanceLayOut() {
        return this.check().getDocument("otherLayouts").getJsonObject("MAINTENANCE");
    }

    public JsonObject getFullLayOut() {
        return this.check().getDocument("otherLayouts").getJsonObject("FULL");
    }

    public JsonArray getOfflineLayOut(){

        return this.check().getJsonArray("loadingLayout");
    }

    public Document getDocument() {
        return document;
    }
}
