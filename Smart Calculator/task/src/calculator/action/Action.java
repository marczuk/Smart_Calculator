package calculator.action;

public class Action {

    public String detectActionFromLine(String line) {

        String lineType;

        if (line.charAt(0) == '/') {
            lineType = "COMMAND";
        }
        else if (line.contains("=")) {
            lineType = "ASSIGN";
        }
        else if (line.matches(".*[\\+\\-\\*\\\\\\^]+.*")) {
            lineType = "CALCULATE";
        }
        else {
            lineType = "CHECK";
        }

        return lineType;
    }
}
