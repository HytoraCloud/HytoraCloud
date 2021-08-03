
package de.lystx.hytoracloud.driver.connection.cloudflare.elements;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CloudFlareResult {


    /**
     * The page
     */
    public Integer page;

    /**
     * The perPage value
     */
    public Integer perPage;

    /**
     * The count value
     */
    public Integer count;

    /**
     * The totalCount value
     */
    public Integer totalCount;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "page", page ).append( "perPage", perPage ).append( "count", count ).append( "totalCount", totalCount ).toString();
    }
}