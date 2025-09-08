package taskmanager.manager;

import taskmanager.model.Node;
import taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> cashe;
    int size;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
        cashe = new HashMap<>();
        size = 0;
    }

    @Override
    public void addTask(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> current = cashe.get(id);
        removeNode(current);
        cashe.remove(id);
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            System.out.println("Объект не должен быть пустым");
            return;
        }
        if (node == head) {
            head = head.next;
            if (head != null) {
                head.prev = null;

            } else {
                tail = null;
            }
            }
            else if (node == tail) {
                tail = tail.prev;
                if (tail != null) {
                    tail.next = null;
                } else {
                    head = null;
                }
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;

            }
            size--;
        }

    public void linkLast(Task data) {
        if (data == null) {
            System.out.println("Объект не должен быть пустым");
            return;
        }
        if (cashe.containsKey(data.getId())) {
            remove(data.getId());
        }
        Node<Task> newNode = new Node<>(data);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;

        cashe.put(data.getId(), newNode);
        size++;

    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }
}



