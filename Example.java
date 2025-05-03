class Client {
    /*@ normal_behaviour
    @  requires t2 != t1;
    @  requires \disjoint(t1.fp, t2.fp);
    @  requires t1.isConst(0);
    @  ensures t1.isConst(1);
    @  ensures t2.isConst(\old(t2.value));
    @*/
    void client(Tree t1, Tree t2) {
        t1.increment();
    }
}

final class Tree {
    /*@ nullable */ Tree left; // this would be @Rep
    /*@ nullable */ Tree right; // this would be @Rep
    public int value;

    // @ public ghost int depth;

    // @ invariant depth >= 0;
    /*@ invariant depth == (left == null && right == null ? 0
    : (left == null ? right.depth + 1
    : (right == null ? left.depth + 1
    : (left.depth > right.depth ? left.depth + 1
    : right.depth + 1)))); */
    // @ invariant depth == 0 <==> (left == null && right == null);

    // @ invariant left != null ==> \invariant_for(left);
    // @ invariant right != null ==> \invariant_for(right);

    // @ invariant left != null && right != null ==> \disjoint(left.fp, right.fp);

    // @ public model \locset fp;
    // @ represents fp = \set_union(this.*, right != null ? right.fp : \empty, left != null ?
    // left.fp : \empty);
    // @ accessible fp: fp;

    /*@ normal_behaviour
    @  requires \disjoint(left.fp, right.fp);
    @  ensures value != v ==> \result == false;
    @  ensures left != null && !left.isConst(v) ==> \result == false;
    @  ensures right != null && !right.isConst(v) ==> \result == false;
    @  accessible value, left.fp, right.fp;
    @  assignable \strictly_nothing;
    @  measured_by depth;
    @*/
    boolean isConst(int v) {
        if (value != v) return false;
        if (left != null && !left.isConst(v)) return false;
        if (right != null && !right.isConst(v)) return false;
        return true;
    }

    /*@ normal_behaviour
    @  requires true;
    @  ensures value == \old(value) + 1;        // Here, something is missing: Unclear how to specify that left/right subtrees are increased ...
    @  assignable value, left.fp, right.fp;
    @  measured_by depth;
    @*/
    void increment() {
        value++;
        if (left != null) left.increment();
        if (right != null) right.increment();
    }
}
