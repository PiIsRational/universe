package typecheck.topol;

import universe.qual.*;

public class PayloadTest {
    void m(@Payload PType i, @Rep PType p) {
        // :: error: (oam.call.forbidden)
        i.hey();

        // :: error: (assignment.type.incompatible)
        @Rep PType j = i;

        // :: warning: (cast.unsafe)
        // :: error: (uts.cast.type.payload.error)
        j = (@Rep PType) i;

        // anything can become a payload
        @Payload PType q = p;

        // should work
        @Payload PType b = i;

        // :: error: (uts.payload.fieldaccess.forbidden)
        var k = i.value;
    }
}

class PType {
    public @Bottom Integer value;

    void hey() {}
}
