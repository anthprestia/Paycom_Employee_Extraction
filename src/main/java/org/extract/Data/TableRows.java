package org.extract.Data;

public class TableRows {
    private boolean complete;
    private String eeTaskId;
    private String taskId;
    private String taskDescription;
    private String taskType;
    private String taskFor;
    private String completedBy;
    private String timeCompleted;
    private boolean startTask;

    public TableRows(boolean complete, String eeTaskId, String taskId, String taskDescription, String taskType,
                     String taskFor, String completedBy, String timeCompleted, boolean startTask) {
        this.complete = complete;
        this.eeTaskId = eeTaskId;
        this.taskId = taskId;
        this.taskDescription = taskDescription;
        this.taskType = taskType;
        this.taskFor = taskFor;
        this.completedBy = completedBy;
        this.timeCompleted = timeCompleted;
        this.startTask = startTask;
    }


    @Override
    public String toString() {
        return this.complete + "," +
                this.eeTaskId + "," +
                this.taskId + "," +
                this.taskDescription + "," +
                this.taskType + "," +
                this.taskFor + "," +
                this.completedBy + "," +
                this.timeCompleted + "," +
                this.startTask + "\n";
    }
}
