package model.adt;

import exceptions.MyException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class MyStack<T> implements MyIStack<T> {
    private Stack<T> stack;

    public MyStack() {
        stack = new Stack<>();
    }

    public T pop() throws MyException {
        if (stack.isEmpty())
            throw new MyException("Stack is empty - cannot pop");
        return stack.pop();
    }

    public void push(T v) {
        stack.push(v);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<T> elements = new ArrayList<>(stack);
        Collections.reverse(elements);
        for (T elem : elements) {
            sb.append(elem.toString()).append("\n");
        }
        return sb.toString();
    }
}
