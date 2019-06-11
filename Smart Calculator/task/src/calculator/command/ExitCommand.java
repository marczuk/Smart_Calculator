package calculator.command;

public class ExitCommand extends Command{

    public ExitCommand() {
        this.exitCommand = true;
    }

    @Override
    public void handle() {
        System.out.println("Bye!");
    }
}
