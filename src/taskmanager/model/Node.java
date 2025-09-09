package taskmanager.model;

public class Node<Task> {
    public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Task data) {
        this.data = data;
    }

}
