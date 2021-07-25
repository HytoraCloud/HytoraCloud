
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Setter @Getter @AllArgsConstructor
public class ZoneComponent {

    /**
     * The name of this component
     */
    private String name;

    /**
     * The default value of this component
     */
    private Integer _default;

    /**
     * The unit price of this component
     */
    private Integer unitPrice;
    
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "name", name ).append( "_default", _default ).append( "unitPrice", unitPrice ).toString();
    }
    
}