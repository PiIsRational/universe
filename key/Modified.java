class Client {
    /*@ normal_behaviour
    @  requires t1.isConst(0);
    @  ensures t1.isConst(1);
    @*/
    void client(Tree t1) {
        t1.increment();
    }
}

final class Tree {
    /*@ nullable */ @Rep Tree left;
    /*@ nullable */ @Rep Tree right;
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

    // @ invariant left != null && right != null ==> left != right;
    // @ invariant left != null && right != null ==> \disjoint(left.fp, right.fp);

    // @ public model \locset fp;
    // @ represents fp = \set_union(this.*, right != null ? right.fp : \empty, left != null ?
    // left.fp : \empty);
    // @ accessible fp: fp;

    /*@ normal_behaviour
    @  requires true;
    @  ensures value != v ==> \result == false;
    @  ensures left != null && !left.isConst(v) ==> \result == false;
    @  ensures right != null && !right.isConst(v) ==> \result == false;
    @  accessible value, left.fp, right.fp; // what would you do here
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
