package de.lystx.hytoracloud.driver.cloudservices.cloud.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ModuleInfo implements Serializable {

    private String name;
    private String author;
    private String version;
    private List<String> commands;
    private ModuleCopyType copyType;


    private File file;

}
