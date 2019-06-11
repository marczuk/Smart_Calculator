package calculator.command;

public class HelpCommand extends Command{

//    static {
//        name = "/help";
//    }
//    protected static String name = "/help";

    private String helpMessage = "The program calculates the sum of numbers //n - calculator support the addition + and subtraction - operators";

    @Override
    public void handle() {
        System.out.println(helpMessage);
    }
}
