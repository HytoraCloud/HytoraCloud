
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.zone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.Identifiable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
public class Owner implements Identifiable {
    
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("owner_type")
    @Expose
    private String ownerType;
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "id", id ).append( "email", email ).append( "ownerType", ownerType ).toString();
    }
    
}
