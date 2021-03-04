package de.lystx.cloudsystem.library.service.config.impl.fallback;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Fallback implements Serializable {

    private final int priority;
    private final String groupName;
    private final String permission;

}
