package gitlet;

import java.util.regex.*;
import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Huang Jinhong
 */
public class Main {
    /**
     * Usage
     * Runs one of 13 commands:
     *
     * init -- Creates a new Gitlet version-control system
     *         in the current directory.
     *
     * add [filename] -- Adds a copy of the file as it currently exists
     *                       to the staging area
     *
     * commit, checkout...
     *
     * ? All persistent data should be stored in a ".capers"
     * ? directory in the current working directory.
     *
     * *YOU SHOULD NOT CREATE THESE MANUALLY,
     *  YOUR PROGRAM SHOULD CREATE THESE FOLDERS/FILES*
     *
     * .gitlet/ -- top level folder for all persistent data in proj2 folder
     *    - commits/ -- folder containing all of the persistent data for commits
     *    - blobs/ -- folder containing all of the persistent data for commits
     *
     * @param args arguments from the command line
     */

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // All error message end with a period
            // get period by my planning? or get period automatically by RuntimeException?
        // If a user doesn’t input any arguments, print the message Please enter a command. and exit.
        if (args.length == 0) {
            printErrorWithExit("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateOperands("init", args, 1);
                Repository.initCommand("initial commit");
                break;
            case "add":
                validateInitAndOperands("add", args, 2);
                Repository.addCommand(args[1]);
                break;
            case "commit":
                validateInitAndOperands("commit", args, 2);
                Repository.commitCommand(args[1]);
                break;
            case "rm":
                validateInitAndOperands("rm", args, 2);
                Repository.rmCommand(args[1]);
                break;
            case "log":
                validateInitAndOperands("log", args, 1);
                Repository.logCommand();
                break;
            case "global-log":
                validateInitAndOperands("global-log", args, 1);
                Repository.globalLogCommand();
                break;
            case "find":
                validateInitAndOperands("global-log", args, 2);
                Repository.findCommand(args[1]);
                break;
            case "status":
                validateInitAndOperands("status", args, 1);
                Repository.statusCommand();
                break;
            case "checkout":
                if (args.length == 2 || args.length == 3 || args.length == 4) {
                    validateInitAndOperands("checkout", args, args.length);
                } else {
                    printErrorWithExit("Incorrect operands.");
                }
                Repository.checkoutCommand(args);
                break;
            case "reset":
                validateInitAndOperands("reset", args, 2);
                Repository.resetCommand(args[1]);
                break;
            case "branch":
                validateInitAndOperands("branch", args, 2);
                Repository.branchCommand(args[1]);
                break;
            case "rm-branch":
                validateInitAndOperands("rm-branch", args, 2);
                Repository.rmBranchCommand(args[1]);
                break;
            case "merge":
                validateInitAndOperands("merge", args, 2);
                Repository.mergeCommand(args[1]);
                break;
            // If a user inputs a command that doesn’t exist,
            // print the message No command with that name exists. and exit.
            default:
                printErrorWithExit("No command with that name exists.");
        }
    }

    /**
     * If a user inputs a command with the wrong number or format of operands,
     * print the message Incorrect operands. and exit.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateOperands(String cmd, String[] args, int n) {
        if (args.length != n) {
            printErrorWithExit("Incorrect operands.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "add":
                matchFileName(args[1]);
                break;
            case "commit":
                if (args[1].equals("")) {
                    printErrorWithExit("Please enter a commit message.");
                }
                matchMessage(args[1]);
                break;
            case "rm":
                matchFileName(args[1]);
                break;
            case "find":
                matchMessage(args[1]);
                break;
            case "checkout":
                // checkout [branch name]
                if (args.length == 2) {
                    matchBranchName(args[1]);
                }
                // checkout -- [file name]
                if (args.length == 3) {
                    matchTwoLines(args[1]);
                    matchFileName(args[2]);
                }
                // checkout [commit id] -- [file name]
                if (args.length == 4) {
                    matchCommitID(args[1]);
                    matchTwoLines(args[2]);
                    matchFileName(args[3]);
                }
                break;
            case "reset":
                matchCommitID(args[1]);
                break;
            case "branch":
                matchBranchName(args[1]);
                break;
            case "rm-branch":
                matchBranchName(args[1]);
                break;
            case "merge":
                matchCommitID(args[1]);
                break;
        }
    }

    public static void validateInitAndOperands(String cmd, String[] args, int n) {
        // validate init
        if (!Repository.validateDirAndFolder()) {
            printErrorWithExit("Not in an initialized Gitlet directory.");
        }
        // validate operands
        validateOperands(cmd, args, n);
    }

    // "Incorrect operands." with Regular Expression of matching filename
    private static void matchFileName(String fileName) {
        // filename pattern
        String fileNamePattern = ".+\\..+";
        if (!Pattern.matches(fileNamePattern, fileName)) {
            printErrorWithExit("Incorrect operands.");
        }
    }

    // "Incorrect operands." with Regular Expression of matching message
    private static void matchMessage(String message) {
        // message pattern
        String messagePattern = ".+";
        if (!Pattern.matches(messagePattern, message)) {
            printErrorWithExit("Incorrect operands.");
        }
    }

    // "Incorrect operands." with Regular Expression of matching "--"
    private static void matchTwoLines(String TwoLines) {
        // two lines pattern
        String twoLinesPattern = "--";
        if (!Pattern.matches(twoLinesPattern, TwoLines)) {
            printErrorWithExit("Incorrect operands.");
        }
    }

    // "Incorrect operands." with Regular Expression of matching message
    private static void matchBranchName(String branchName) {
        // message pattern equals branch name pattern
        matchMessage(branchName);
    }

    // "Incorrect operands." with Regular Expression of matching commitID
    private static void matchCommitID(String message) {
        // commit id pattern
        String commitIDPattern = "[\\w\\d]+";
        if (!Pattern.matches(commitIDPattern, message)) {
            printErrorWithExit("Incorrect operands.");
        }
    }
}
