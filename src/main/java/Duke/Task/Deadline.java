package Duke.Task;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A Specific type of task that contains a task description and an end date.
 */
public class Deadline extends Task {
    public static final String taskType = "deadline";
    public static final String symbol = "D";

    protected LocalDate endDate;

    public Deadline(String description, LocalDate endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Encodes Deadline data for file saving.
     *
     * @return Encoded Deadline data in String format.
     */
    @Override
    public String convertToSaveFormat() {
        String doneMarker = isDone ? "1" : "0";
        String endDateString = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return symbol + " | " + doneMarker + " | " + description + " /by " + endDateString;
    }

    /**
     * Override the printing of Deadline.
     *
     * @return String format of Deadline for printing.
     */
    @Override
    public String toString() {
        String endDateString = endDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        return "\t[" + symbol + "]" + super.toString() + " (by: " + endDateString + ")";
    }

}
