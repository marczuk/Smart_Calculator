package calculator.command;

public abstract class Command {

    protected boolean exitCommand = false;

    public abstract void handle();

//    public static String getName() {
//        return name;
//    }

    public boolean isExitCommand() {
        return exitCommand;
    }
}
