package typecheck.topol;

import universe.qual.*;

public class GenericPayload {
    void m() {
        @Rep Cell<Integer> myInt = new @Rep Cell<Integer>(0);
        @Rep Cell<@Rep Cell<Integer>> c = new @Rep Cell<@Rep Cell<Integer>>(myInt);
        @Rep Cell<Integer> i = c.get();
    }
}

// the payload extention is not needed here, since @Payload Object is already
// the upper bound!
class Cell<T extends @Payload Object> {
    private T val;

    public Cell(T x) {
        val = x;
    }

    public T get() {
        return val;
    }

    public void set(T val) {
        this.val = val;
    }
}

class Client {
    void append(@Rep Tree<Integer> t1, int value, @Rep Tree<Integer> t2) {
        t1.append(value);
    }
}

final class Tree<T extends @Payload Comparable<T>> {
    public @Rep Tree<T> left;
    public @Rep Tree<T> right;
    public T value;

    public Tree(T v) {
        left = null;
        right = null;
        value = v;
    }

    public boolean isConst(T v) {
        if (value.compareTo(v) != 0) return false;
        if (left != null && !left.isConst(v)) return false;
        if (right != null && !right.isConst(v)) return false;
        return true;
    }

    public boolean contains(T v) {
        if (value == v) return true;
        if (left != null && left.contains(v)) return true;
        if (right != null && right.contains(v)) return true;
        return false;
    }

    public void append(T v) {
        if (v.compareTo(value) <= 0) {
            if (left == null) {
                left = new @Rep Tree(v);
            } else {
                left.append(v);
            }
        } else {
            if (right == null) {
                right = new @Rep Tree(v);
            } else {
                right.append(v);
            }
        }
    }
}
