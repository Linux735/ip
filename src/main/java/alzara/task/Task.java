package alzara.task;

public class Task {
    private String task;
    private boolean done;

    private Task(String task , boolean done) {
        this.task = task;
        this.done = done;
    }

    public Task(String task) {
        this(task, false);
    }

    public void mark(int index) {
        this.done = true;
    }

    public void unmark(int index) {
        this.done = false;
    }

    public String toSaveFormat() {
        return (this.done ? "Y" : "N") + " | " + this.task;
    }

    @Override
    public String toString() {
        if (this.done) {
            return "[X] " + this.task;
        }
        return "[ ] " + this.task;
    }
}
