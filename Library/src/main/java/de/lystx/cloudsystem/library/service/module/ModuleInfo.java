package de.lystx.cloudsystem.library.service.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter @Setter
@RequiredArgsConstructor
public class ModuleInfo {

    private final String name;
    private final String author;
    private final String version;
    private final List<String> commands;
    private final boolean copy;


    private File file;
}
