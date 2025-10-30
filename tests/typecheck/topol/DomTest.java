package typecheck.topol;

import universe.qual.Dom;
import universe.qual.Rep;
import universe.qual.Payload;

class List {
    @Dom Node first;
    @Dom Node last;
    int size;

    public List() {
        first = null;
        last = null;
        size = 0;
    }

    public int size() {
        return size;
    }


    public @Payload Object get(int index) {
        if(index < 0 || size <= index) {
            throw new IndexOutOfBoundsException();
        }

        @Dom Node node = first;
        for(int i = 0; i < index; i++) {
            node = node.next;
        }

        return node.data;
    }
    
    public void set(int index, @Payload Object o) {
        if(index < 0 || size <= index) {
            throw new IndexOutOfBoundsException();
        }

        @Dom Node node = first;
        for(int i = 0; i < index; i++) {
            node = node.next;
        }

        node.data = o;
    }

    public boolean contains(@Payload Object o) {
        if(size == 0) {
            return false;
        }

        @Dom Node node = first;
        for (int i = 0; i < size - 1; i++) {
            if (node.data == o) return true;
            node = node.next;
        }

        return node.data == o;
    }


    public void add(@Payload Object o) {
        @Rep Node node = new @Rep Node(o);
        if(size == 0) {
            first = node;
            last = node;
        } else {
            last.next = node;
            last = last.next;
        }

        size++;
    }

    public @Payload Object pop() {
        if (last == null) {
            throw new RuntimeException();
        }

        @Dom Node popped = last;
        size--;
        if (size == 0) {
            first = null;
            last = null;
            return popped;
        } 

        @Dom Node node = first;
        if (size > 1) {
            for(int i = 0; i < size - 1; i++) node = node.next;
        }
        
        node.next = null;
        last = node;
        
        return last;
    }
}

class Node {
    @Dom Node next;
    @Payload Object data;

    public Node(@Payload Object o) {
        next = null;
        data = o;
    }
}

