package calculator.service;

import calculator.exception.InvalidInputException;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    final String operatorPlus = "PLUS";
    final String operatorMinus = "MINUS";
    final String operatorMultiply = "MULTIPLY";
    final String operatorDivision = "DIVISION";
    final String operatorPower = "POWER";

    final String errorMessage = "Invalid expression";

    Map<String, BigInteger> vars = new HashMap<>();

    public void assignFromString(String text) throws InvalidInputException {
        //remove all spaces
        text = text.replaceAll("\\s", "");

        String[] parts = text.split("=");

        if (parts.length != 2) {
            throw new InvalidInputException("Invalid assignment");
        }

        //validate parts
        if (!parts[0].matches("[a-zA-Z]+")) {
            throw new InvalidInputException("Invalid identifier");
        }

        try {

            // add to set
            BigInteger value = getValueFromString(parts[1]);
            vars.remove(parts[0]);
            vars.put(parts[0], value);

        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid assignment");
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public BigInteger getValueFromString(String text) throws InvalidInputException {
        BigInteger value;
        if (text.matches("[a-zA-Z]+")) {
            if (vars.containsKey(text)) {
                value = vars.get(text);
            } else {
                throw new InvalidInputException("Unknown variable");
            }
        } else if (text.matches("[0-9]+")) {
            value = new BigInteger(text);
        } else {
            throw new InvalidInputException("Invalid assignment");
        }

        return value;
    }

    public BigInteger advancedCalculateFromString(String text) throws InvalidInputException {
        BigInteger result;
        boolean isNumb = true;
        String operator = operatorPlus;

        //text = "1 +++ (223 * 3) -- 4 --- av";
        //text = "4 + 6 - 8";
        //text = "3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1)";
        //text = "4 + 3)";

        //prepare line to operate
        text = text.replaceAll("\\s+", "");
        //text = text.replaceAll("(\\-[^\\-]*\\-[^\\-]*)*", "+");
        text = text.replaceAll("\\++", "+");
        //text = text.replaceAll("\\-+", "-");
        text = removeDuplicatedMinusOp(text);

//        System.out.println(text);

        //split by operator and keep operators
        String[] parts = text.split("(?<=[-+*/)(^])|(?=[-+*/)(^])");

        //convert to postfix
        text = convertInfixToPostfix(parts);

//        System.out.println(text);

        result = calculateFromPostfixString(text);

        //System.out.println(Arrays.toString(parts));




        return result;
    }

    private BigInteger calculateFromPostfixString(String text) throws InvalidInputException {
        BigInteger result;

        String operator;
        Deque<BigInteger> stack = new ArrayDeque<>();

        try {
            String[] parts = text.split("\\s+");
            for (String part : parts) {
                if (part.matches("[]a-zA-Z0-9]+")) {
                    BigInteger number = getValueFromString(part);
                    stack.offerLast(number);
                }
                else {
                    //operator
                    operator = getOperator(part);

                    //should be two numbers on stack
                    BigInteger rightOperand = stack.pollLast();
                    BigInteger leftOperand = stack.pollLast();

                    //operation
                    switch (operator) {

                        case operatorPlus:
                            leftOperand = leftOperand.add(rightOperand);
                            break;
                        case operatorMinus:
                            leftOperand = leftOperand.subtract(rightOperand);
                            break;
                        case operatorDivision:
                            leftOperand = leftOperand.divide(rightOperand);
                            break;
                        case operatorMultiply:
                            leftOperand = leftOperand.multiply(rightOperand);
                            break;
                        case operatorPower:
                            leftOperand = leftOperand.modPow(rightOperand, BigInteger.ONE);
                            break;
                    }
                    stack.offerLast(leftOperand);
                }
            }
            result = stack.pollLast();
        } catch (InvalidInputException e) {
//            System.out.println("dvxvxvc");
//            System.out.println(e.getMessage());
//            e.printStackTrace();
            throw new InvalidInputException(e.getMessage());
        }


        return result;
    }

    private String convertInfixToPostfix(String[] parts) throws InvalidInputException {
        try {
            StringBuilder result = new StringBuilder();

            Deque<String> stack = new ArrayDeque<>();

            for (String part : parts) {

                //detect operator
                if (part.matches("[\\+\\-\\*/]")) {
                    if (stack.isEmpty() || stack.peekLast().equals("(")) {
                        stack.offerLast(part);
                    } else {
                        int lastPrecedence = precedenceLevel(stack.peekLast().charAt(0));
                        if (precedenceLevel(part.charAt(0)) > lastPrecedence) {
                            stack.offerLast(part);
                        } else {
                            String lastChar = stack.peekLast();
                            while (!stack.isEmpty()
                                    && !lastChar.equals("(")
                                    && precedenceLevel(part.charAt(0)) <= precedenceLevel(lastChar.charAt(0))) {

                                result.append(lastChar).append(" ");

                                stack.removeLast();
                                lastChar = stack.peekLast();

                            }
                            stack.offerLast(part);
                        }
                    }
                } else if (part.equals(")")) {
                    //pop until '(' or empty
                    String lastChar = stack.pollLast();

                    while (lastChar != null && !lastChar.equals("(")) {
                        result.append(lastChar).append(" ");
                        lastChar = stack.pollLast();
                    }
                    if(lastChar == null) {
                        throw new InvalidInputException(errorMessage);
                    }
                } else if (part.equals("(")) {
                    stack.offerLast(part);
                } else {
                    //if numb or var just put
                    result.append(part).append(" ");
                }

            }

            int lastStackSize = stack.size();
            //pop rest of the stack
            for (int i = 0; i < lastStackSize; i++) {
                result.append(stack.pollLast()).append(" ");
            }



            return result.toString();
        }
        catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    private int precedenceLevel(char op) {
        switch (op) {
            case '+':
            case '-':
                return 0;
            case '*':
            case '/':
                return 1;
            case '^':
                return 2;
            default:
                throw new IllegalArgumentException("Operator unknown: " + op);
        }
    }

    private String removeDuplicatedMinusOp(String text) {
        StringBuilder result = new StringBuilder();

        int numberOfMinus = 0;
        for (int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            //Process char
            if (c == '-') {
                numberOfMinus++;
            }
            else {
                if (numberOfMinus > 0){
                    if (numberOfMinus%2 == 0){
                        result.append('+');
                    }
                    else {
                        result.append('-');
                    }

                    numberOfMinus = 0;
                }
                result.append(c);
            }
        }
        if (numberOfMinus > 0){
            if (numberOfMinus%2 == 0){
                result.append('+');
            }
            else {
                result.append('-');
            }
        }

        return result.toString();
    }

    public String getOperator(String part) throws InvalidInputException {
        String operator = operatorPlus;

        char minusChar = '-';
        char plusChar = '+';
        char multiplyChar = '*';
        char divisionChar = '/';
        char powerChar = '^';

        int minusCount = countMatches(part, minusChar);

        if (countMatches(part, multiplyChar) > 0) {
            operator = operatorMultiply;
        }
        else if (countMatches(part, divisionChar) > 0) {
            operator = operatorDivision;
        }
        else if (countMatches(part, powerChar) > 0) {
            operator = operatorPower;
        }
        else if (minusCount >= 1) {
            operator = operatorMinus;

            //two minuses makes plus
            if (minusCount%2 == 0) {
                operator = operatorPlus;
            }
        } else {
            //check we have a plus - otherwise error
            if (countMatches(part, plusChar) == 0) {
                throw new InvalidInputException(errorMessage);
            }
        }

        return operator;
    }

    private int countMatches(String word, char character) {
        Pattern pattern = Pattern.compile(String.format("\\%s", character));
        Matcher matcher = pattern.matcher(word);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

}
