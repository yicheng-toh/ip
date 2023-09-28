package Duke.Command;

import Duke.Exception.InvalidDateTimeSpecifiedException;
import Duke.Exception.NoTaskSpecifiedException;
import Duke.Task.*;
import Duke.Ui.Ui;

import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Command {

    //TODO need to figure out how to eliminate the ui here
    private static final Ui ui = new Ui();
//    public Command(){
//        ui = new Ui();
//    }




    public static Task createDeadline(String removedInstructionString)
            throws NoTaskSpecifiedException, InvalidDateTimeSpecifiedException {
        //TODO need to catch lack of by here.
        String taskDescription;
        String byDateString;
        LocalDate byDate;
        try {
            String dateIndicator = "/by";
            String[] deadlineContents = removedInstructionString.split(dateIndicator, 2);
            taskDescription = deadlineContents[0].trim();
            byDateString = deadlineContents[1].trim();
            byDate = LocalDate.parse(byDateString);

        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new InvalidDateTimeSpecifiedException();
        }
        if (taskDescription.isEmpty()) {
            throw new NoTaskSpecifiedException();
        }

        return new Deadline(taskDescription, byDate);
    }

    public static Task createToDo(String taskDescription) throws NoTaskSpecifiedException {
        taskDescription = taskDescription.trim();
        if (taskDescription.isEmpty()) {
            throw new NoTaskSpecifiedException();
        }
        return new ToDo(taskDescription);
    }

    public static void executeMarkCommand(String removedInstructionString, TaskList records) {
        int taskNum;
        try {
            taskNum = Integer.parseInt(removedInstructionString);
            records.getTask(taskNum).setDone();
            executeListCommand(records);
        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer");

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            if (records.getNumTask() == 0) {
                System.out.println("Please create a task to continue");
            } else if (records.getNumTask() == 1) {
                System.out.println("There are " + records.getNumTask() + " task.");
                System.out.println("Please enter 'mark 1' to check the first task as completed");
            } else {
                System.out.println("There are " + records.getNumTask() + " tasks.");
                System.out.println("Please enter a valid number from 1 to " + records.getNumTask() + "(inclusive).");
            }
        }
    }

    public static void executeUnmarkCommand(String removedInstructionString, TaskList records) {
        int taskNum;
        try {
            taskNum = Integer.parseInt(removedInstructionString);
            records.getTask(taskNum).setUndone();
            executeListCommand(records);
        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            if (records.getNumTask() == 0) {
                System.out.println("Please create a task to continue");
            } else if (records.getNumTask() == 1) {
                System.out.println("There are " + records.getNumTask() + " task.");
                System.out.println("Please enter 'mark 1' to check the first task as completed");
            } else {
                System.out.println("There are " + records.getNumTask() + " tasks.");
                System.out.println("Please enter a valid number from 1 to " + records.getNumTask() + "(inclusive).");
            }
        }
    }

    public static void createNewTask(String instructionString, String removedInstructionString,
                              TaskList records) {
        Task task = null;
        try {
            switch (instructionString) {
            case "todo":
                try {
                    task = createToDo(removedInstructionString);
                } catch (NoTaskSpecifiedException e) {
                    System.out.println("Please indicate the task for todo.");
                }
                break;
            case "deadline":
                try {
                    task = createDeadline(removedInstructionString);
                } catch (NoTaskSpecifiedException e) {
                    System.out.println("Please indicate the task for deadline.");
                } catch (InvalidDateTimeSpecifiedException e) {
                    System.out.println("Please indicate the end date for this deadline in yyyy-mm-dd format");
                }
                break;
            case "event":
                try {
                    task = createEvent(removedInstructionString);
                } catch (NoTaskSpecifiedException e) {
                    System.out.println("Please indicate the task for event.");
                } catch (InvalidDateTimeSpecifiedException e) {
                    System.out.println("Please indicate the start date and end date in yyyy-mm-dd format for this event.");
                }
                break;
            default:
                System.out.println("Command is invalid!");
                break;
            }
            if (task != null) {
                addTaskToList(task, records);
                printTaskAdded(task, records);
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Please ensure that " + instructionString + " has the correct number of parameters.");
        }
    }

    public static void deleteTask(String indexString, TaskList records) {
        try {
            int taskIndex = Integer.parseInt(indexString);
            Task task = records.getTask(taskIndex);
            records.deleteTask(taskIndex);
            ui.printLine();
            System.out.println("\tNoted. I've removed this task:");
            System.out.println(task);
            System.out.println("\tNow you have " + records.getNumTask() + " tasks in the list.");
            ui.printLine();
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("There are only " + records.getNumTask() + " tasks.");
        }
    }

    public void saveTaskList(String filePath, TaskList records) throws IOException {
        String taskSaveFormat;
        FileWriter fw = new FileWriter(filePath);

        for (int i = 1; i < records.getNumTask() + 1; i++) {
            taskSaveFormat = records.getTask(i).convertToSaveFormat();
            fw.write(taskSaveFormat + "\n");
        }
        fw.close();
    }

    public static void executeListCommand(TaskList records) {
        ui.printLine();
        for(int i = 1; i < records.getNumTask() + 1 ; i++){
            System.out.println(records.getTask(i));
        }
        ui.printLine();
    }

    public static Task createEvent(String removedInstructionString) throws NoTaskSpecifiedException, InvalidDateTimeSpecifiedException {
        String taskDescription;
        String fromDateString;
        String toDateString;
        String startDateIndicator = "/from";
        String endDateIndicator = "/to";
        LocalDate fromDate;
        LocalDate toDate;
        String eventSplitNotation = startDateIndicator + "|" + endDateIndicator;
        String[] eventContents = removedInstructionString.split(eventSplitNotation);
        try {
            taskDescription = eventContents[0].trim();
            fromDateString = eventContents[1].trim();
            toDateString = eventContents[2].trim();
            fromDate = LocalDate.parse(fromDateString);
            toDate = LocalDate.parse(toDateString);
            
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new InvalidDateTimeSpecifiedException();
        }
        if (taskDescription.isEmpty()) {
            throw new NoTaskSpecifiedException();
        }
     
        return new Event(taskDescription, fromDate, toDate);
    }

    public static void printTaskAdded(Task task, TaskList records) {
        ui.printLine();
        System.out.println("\tGot it. I've added this task:");
        System.out.println("\t" + task);
        System.out.println("\tNow you have " + records.getNumTask() + " tasks in the list.");
        ui.printLine();
    }

    public static void addTaskToList(Task task, TaskList records) {
        records.addTask(task);
    }
}
