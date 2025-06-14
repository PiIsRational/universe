import universe.qual.Rep;

class Client {
    /*@ normal_behaviour
    @  requires t1 != t2;
    @  requires t2.isConst(t2.value);
    @  requires \invariant_for(t1);
    @  requires \invariant_for(t2);
    @  requires t1.isConst(0);
    @  ensures t1.isConst(1);
    @  ensures t2.isConst(\old(t2.value));
    @*/
    void client(@Rep Tree t1, @Rep Tree t2) {
        t1.increment();
    }
}

final class Tree {
    // # owns(this, left) & owns(this, right);
    public /*@ nullable */ @Rep Tree left;
    public /*@ nullable */ @Rep Tree right;
    public int value;

    // @ public ghost int depth;

    // @ public invariant depth >= 0;
    /*@ public invariant depth == (left == null && right == null ? 0
    : (left == null ? right.depth + 1
    : (right == null ? left.depth + 1
    : (left.depth > right.depth ? left.depth + 1
    : right.depth + 1)))); */
    // @ public invariant depth == 0 <==> (left == null && right == null);

    // @ public invariant left != null ==> \invariant_for(left);
    // @ public invariant right != null ==> \invariant_for(right);

    // @ public invariant left != null && right != null ==> left != right;

    // @ public model \locset fp;
    // @ public represents fp = \set_union(this.*, repfp);
    // # dominatesSet(self, repfp);
    // @ public model \locset repfp;
    // @ represents repfp = \set_union(right != null ? right.fp : \empty, left != null ? left.fp :
    // \empty);
    // @ accessible repfp: repfp;
    // @ accessible \inv: fp;

    /*@ normal_behaviour
    @  requires true;
    @  ensures (\result == true) <==>
        (\forall Tree o; \subset(\singleton(o.value), fp); o.value == v);
    @  assignable \strictly_nothing;
    @  accessible fp;
    @  measured_by depth;
    @*/
    public boolean isConst(int v) {
        if (value != v) return false;
        if (left != null && !left.isConst(v)) return false;
        if (right != null && !right.isConst(v)) return false;
        return true;
    }

    /*@ normal_behaviour
    @  requires true;
    @  ensures (\forall Tree t; \subset(\singleton(t.value), fp); t.value == \old(t.value) + 1);
    @  ensures fp == \old(fp);
    @  assignable fp;
    @  accessible fp;
    @  measured_by depth;
    @*/
    void increment() {
        value++;
        if (left != null) left.increment();
        if (right != null) right.increment();
    }

    /*@ normal_behaviour
    @  requires true;
    @  ensures \result == left;
    @*/
    public @Rep Tree getLeft() {
        @Rep Tree l = left;
        return l;
    }
}
