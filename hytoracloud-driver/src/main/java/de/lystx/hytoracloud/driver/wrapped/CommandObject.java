package de.lystx.hytoracloud.driver.wrapped;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CommandObject extends WrappedObject<ICommand, CommandObject> implements ICommand {

    private static final long serialVersionUID = -4609864936387688763L;
    /**
     * The name of the command
     */
    private final String name;

    /**
     * The description to give info
     */
    private final String description;

    /**
     * Aliases to access this command
     */
    private final String[] aliases;

    @Override
    public void execute(CommandExecutor executor, String[] args) {
        CommandListener listener = CloudDriver.getInstance().getCommandManager().getListener(name);
        if (listener != null) {
            listener.execute(executor, args);
        }
    }

    @Override
    Class<CommandObject> getWrapperClass() {
        return CommandObject.class;
    }

    @Override
    Class<ICommand> getInterface() {
        return ICommand.class;
    }
}
