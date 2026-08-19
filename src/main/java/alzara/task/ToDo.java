package alzara.task;

public class ToDo extends Task{
    public ToDo(String task) {
        super(task);
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
