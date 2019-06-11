package calculator;

import calculator.action.Action;
//import calculator.action.ActionTypeEnum;
import calculator.command.Command;
import calculator.command.CommandFactory;
import calculator.exception.InvalidInputException;
import calculator.service.Calculator;

import java.math.BigInteger;
import java.util.Scanner;


public class Main {

    Calculator calculator;
    Scanner scanner;
    CommandFactory commandFactory;

    boolean triggerNextLine = true;

    public Main() {
        this.calculator = new Calculator();
        this.scanner = new Scanner(System.in);
        this.commandFactory = new CommandFactory();
    }

    public static void main(String[] args) {
        Main application = new Main();
        application.start();
    }

    public void start() {
        executeLine(getNextLine());
    }

    private void executeLine(String nextLine)
    {
        if (!nextLine.isEmpty()) {
            Action action = new Action();
            String actionType = action.detectActionFromLine(nextLine);

            try {
                switch (actionType) {
                    case "COMMAND":
                        startCommand(nextLine);
                        break;

                    case "CALCULATE":
                        startCalculate(nextLine);
                        break;

                    case "ASSIGN":
                        startAssign(nextLine);
                        break;
                    case "CHECK":
                        startCheck(nextLine);
                        break;

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
        }

        if (triggerNextLine) {
            executeLine(getNextLine());
        }
    }

    private void startCommand(String line) throws InvalidInputException {
        Command command = commandFactory.getCommand(line);
        command.handle();

        if (command.isExitCommand()) {
            triggerNextLine = false;
        }
    }

    private void startCalculate(String line) throws Exception {
        BigInteger result = calculator.advancedCalculateFromString(line);
        //int result = 0;
        System.out.println(result);
    }

    private void startCheck(String line) throws Exception {
        BigInteger result = calculator.getValueFromString(line);
        System.out.println(result);
    }

    private void startAssign(String line) throws Exception {
        calculator.assignFromString(line);
    }

    private String getNextLine() {
        return scanner.nextLine();
    }


}
