package calculator.command;

import calculator.exception.InvalidInputException;

public class CommandFactory {

    private final String unknownMessage = "Unknown command";

    private final String helpCommand = "/help";
    private final String stopCommand = "/exit";

    public Command getCommand(String textCommand) throws InvalidInputException {

        Command command;
        if (textCommand.equals(helpCommand)){
            command = new HelpCommand();
        }
        else if (textCommand.equals(stopCommand)){
            command = new ExitCommand();
        }
        else {
            throw new InvalidInputException(unknownMessage);
        }


        return command;
    }
}
