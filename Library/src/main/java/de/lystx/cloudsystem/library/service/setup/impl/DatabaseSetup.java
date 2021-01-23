package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class DatabaseSetup extends Setup {

    @SetupPart(question = "What's the host of your database?", id = 1)
    private String host;

    @SetupPart(question = "What's the port of your database?", id = 2)
    private int port;

    @SetupPart(question = "What's the username of your database?", id = 3)
    private String username;

    @SetupPart(question = "What's the default database?", id = 4)
    private String defaultDatabase;

    @SetupPart(question = "What's the collection (MongoDB) or table (MySQL) of your database?", id = 5)
    private String collectionOrTable;

    @SetupPart(question = "What's the password of your database?", id = 6)
    private String password;
}
