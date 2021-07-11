package de.lystx.hytoracloud.launcher.global.impl.setup;

import de.lystx.hytoracloud.driver.utils.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.utils.setup.Setup;
import lombok.Getter;

@Getter
public class DatabaseSetup extends AbstractSetup<DatabaseSetup> {

    @Setup(question = "What's the host of your database?", id = 1)
    private String host;

    @Setup(question = "What's the port of your database?", id = 2)
    private int port;

    @Setup(question = "What's the username of your database?", id = 3)
    private String username;

    @Setup(question = "What's the default database?", id = 4)
    private String defaultDatabase;

    @Setup(question = "What's the collection (MongoDB) or table (MySQL) of your database?", id = 5)
    private String collectionOrTable;

    @Setup(question = "What's the password of your database?", id = 6)
    private String password;
}
