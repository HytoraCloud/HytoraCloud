
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.user.billing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.Identifiable;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.zone.Zone;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
public class Billing implements Identifiable {
    
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("occurred_at")
    @Expose
    private String occurredAt;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("zone")
    @Expose
    private Zone zone;
    
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "id", id ).append( "type", type ).append( "action", action ).append( "description", description ).append( "occurredAt", occurredAt ).append( "amount", amount ).append( "currency", currency ).append( "zone", zone ).toString();
    }
}