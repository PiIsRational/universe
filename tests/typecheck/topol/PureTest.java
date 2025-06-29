import org.checkerframework.dataflow.qual.*;

import universe.qual.*;

public class PureTest {
    @Rep PureTest a;

    // :: warning: (purity.deterministic.void.method)
    // :: error: (purity.not.deterministic.not.sideeffectfree.assign.field)
    @Pure
    public void foo(@Rep PureTest a) {
        this.a = a;
    }
}
