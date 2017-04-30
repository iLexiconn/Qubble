package net.ilexiconn.qubble.client.project.action;

public class ActionHistory {
    private EditAction[] stack;
    private int capacity;
    private int top;
    private int pushed = 0;

    public ActionHistory(int capacity) {
        this.stack = new EditAction[capacity];
        this.top = 0;
        this.capacity = capacity;
    }

    public void push(EditAction action) {
        if (this.top == this.stack.length) {
            this.top = 0;
        }
        this.stack[this.top] = action;
        this.top++;
        if (this.pushed < this.capacity - 1) {
            this.pushed++;
        }
    }

    public EditAction pop() {
        if (this.pushed > 0) {
            if (this.top - 1 < 0) {
                this.top = this.capacity;
            }
            this.top--;
            EditAction top = this.stack[this.top];
            this.pushed--;
            return top;
        }
        return null;
    }

    public void clear() {
        this.top = 0;
    }

    public int capacity() {
        return this.capacity;
    }
}