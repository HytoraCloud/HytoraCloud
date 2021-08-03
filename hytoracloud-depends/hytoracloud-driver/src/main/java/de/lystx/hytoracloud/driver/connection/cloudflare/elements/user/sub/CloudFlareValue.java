
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.sub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Getter @Setter @AllArgsConstructor
public class CloudFlareValue {

    /**
     * The name of the value
     */
    private String name;

    /**
     * The integer value
     */
    private Integer value;

    /**
     * The default integer value
     */
    private Integer _default;

    /**
     * The price
     */
    private Integer price;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "name", name ).append( "value", value ).append( "_default", _default ).append( "price", price ).toString();
    }
    
}
