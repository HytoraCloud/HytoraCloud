
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;


@Setter @Getter
public class ZoneSetting implements CloudFlareable {

    /**
     * The id of this settings
     */
    private String id;

    /**
     * The last modification date of this settings
     */
    private String modifiedOn;

    /**
     * If this settings are editable
     */
    private Boolean editable;

    /**
     * The value of this settings
     */
    private String value;

    /**
     * The extra values of this settings
     */
    private Map<String, String> additional;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "modifiedOn", modifiedOn ).append( "editable", editable ).append( "value", value ).append( "additional", additional ).toString();
        
    }
    
}