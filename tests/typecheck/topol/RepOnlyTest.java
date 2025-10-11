package typecheck.topol;

import universe.qual.*;

public class RepOnlyTest {
    @Rep Under u;
    @Peer Under i;
    @Payload Under p;

    @RepOnly
    void m() {
        // :: error: (uts.reponly.call.self.forbidden)
        n();

        // should workd
        k();

        // :: error: (uts.reponly.call.self.forbidden)
        this.n();

        // should work
        this.k();

        // should work
        u.hey();

        // :: error: (uts.reponly.call.other.forbidden)
        i.hey();

        // :: error: (uts.reponly.call.other.forbidden)
        i.bar();

        @Payload Under q = p;
        p = q;
    }

    void n() {}

    @RepOnly
    void k() {}
}

class Under {
    public void hey() {}

    @RepOnly
    public void bar() {}
}
