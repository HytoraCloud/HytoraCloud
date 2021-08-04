package de.lystx.hytoracloud.driver.serverselector.sign;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignAnimation implements Serializable {

    private static final long serialVersionUID = 6458180607615034279L;

    /**
     * The update tick of signs
     */
    private final int repeatingTick;

    /**
     * All sign layouts
     */
    private final SignLayout[] signLayouts;

    public SignAnimation(int repeatingTick, SignLayout... signLayouts) {
        this.repeatingTick = repeatingTick;
        this.signLayouts = signLayouts;
    }

    /**
     * The size of this animation
     *
     * @return the size as int
     */
    public int size() {
        return this.signLayouts.length;
    }

    /**
     * Gets a {@link SignLayout} by id
     *
     * @param i the id
     * @return layout
     */
    public SignLayout get(int i) {
        return this.signLayouts[i];
    }
}
