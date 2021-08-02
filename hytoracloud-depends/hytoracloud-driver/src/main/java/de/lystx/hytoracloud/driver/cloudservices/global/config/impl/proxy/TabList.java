package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @AllArgsConstructor @Setter
public class TabList implements Serializable {

    private static final long serialVersionUID = -3372478432974039029L;
    /**
     * If the tablist is enabled
     */
    private final boolean enabled;

    /**
     * The header
     */
    private String[] headerLines;

    /**
     * The footer
     */
    private String[] footerLines;


    /**
     * Formats the headerLines-array
     * into a single {@link String}
     *
     * @return string
     */
    public String headerToSingleString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < headerLines.length; i++) {
            String headerLine = headerLines[i];
            stringBuilder.append(headerLine);
            if (!((i + 1) >= headerLines.length)) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Formats the footerLines-array
     * into a single {@link String}
     *
     * @return string
     */
    public String footerToSingleString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < footerLines.length; i++) {
            String footerLine = footerLines[i];
            stringBuilder.append(footerLine);
            if (!((i + 1) >= footerLines.length)) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
