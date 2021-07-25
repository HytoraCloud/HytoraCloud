
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.Identifiable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@Getter
@Setter
public class UserOrganization implements Identifiable {
    
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("permissions")
    @Expose
    private List<String> permissions = null;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "Status", status ).append( "permissions", permissions ).append( "roles", roles ).toString();
    }
    
}