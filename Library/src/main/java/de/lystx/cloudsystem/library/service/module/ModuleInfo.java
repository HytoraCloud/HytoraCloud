package de.lystx.cloudsystem.library.service.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModuleInfo {

    private final String name;
    private final String author;
    private final String version;
    private final List<String> commands;
}
