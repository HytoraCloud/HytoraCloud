
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResultInfo {
    

    @Expose @SerializedName("page")
    public Integer page;

    @Expose @SerializedName("per_page")
    public Integer perPage;

    @Expose @SerializedName("count")
    public Integer count;

    @Expose @SerializedName("total_count")
    public Integer totalCount;
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "page", page ).append( "perPage", perPage ).append( "count", count ).append( "totalCount", totalCount ).toString();
    }
}