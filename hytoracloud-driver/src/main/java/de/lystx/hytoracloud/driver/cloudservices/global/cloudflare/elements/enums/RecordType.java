
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum RecordType {
    
    A( "IPV4Address" ),
    AAAA( "IPV6Address" ),
    CNAME( "CanonicalName" ),
    MX( "MailExchanger" ),
    NS( "NameServer" ),
    SRV( "ServiceSupport" ),
    TXT( "Text" ),
    LOC( "Localisation" ),
    SPF( "SenderPolicyFramework" ),
    CAA( "CertificationAuthorityAuthorization" );

    private final String fullName;

}
