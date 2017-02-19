package quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import quickfix.field.EffectiveTime;
import quickfix.field.MDEntryTime;
import quickfix.field.converter.UtcTimeOnlyConverter;

import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Tests the {@link FieldMap} class.
 * Specifically, verifies that the setters for {@link UtcTimeStampField} work correctly.
 *
 * @author toli
 * @version $Id$
 */
public class FieldMapTest extends TestCase {
    public FieldMapTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(FieldMapTest.class);
    }

    public void testSetUtcTimeStampField() throws Exception {
        FieldMap map = new Message();
        Timestamp aDate = new Timestamp(System.currentTimeMillis());
        map.setField(new UtcTimeStampField(EffectiveTime.FIELD, aDate, false, false));
        Timestamp actual = map.getField(new EffectiveTime()).getValue();
        assertEquals("milliseconds should not be preserved", aDate.getTime() - (aDate.getTime() % 1000),
        		actual.getTime());
        assertEquals("milliseconds should not be preserved", 0, actual.getNanos());

        // now set it with preserving millis
        map.setField(new UtcTimeStampField(EffectiveTime.FIELD, aDate, true, false));
        actual = map.getField(new EffectiveTime()).getValue();
        assertEquals("milliseconds should be preserved", aDate.getTime(),
                    actual.getTime());
        assertEquals("milliseconds should be preserved", (aDate.getTime() % 1_000) * 1_000_000, actual.getNanos());
        
     // now set it with preserving micros
        map.setField(new UtcTimeStampField(EffectiveTime.FIELD, aDate, true, true));
        actual = map.getField(new EffectiveTime()).getValue();
        assertEquals("microseconds should be preserved", aDate.getTime(),
                    map.getField(new EffectiveTime()).getValue().getTime());
        assertEquals("microseconds should be preserved", aDate.getNanos(), actual.getNanos());
        
        map.setField(new UtcTimeStampField(EffectiveTime.FIELD, aDate, false, true));
        actual = map.getField(new EffectiveTime()).getValue();
        assertEquals("microseconds should be preserved", aDate.getTime(),
                    map.getField(new EffectiveTime()).getValue().getTime());
        assertEquals("microseconds should be preserved", aDate.getNanos(), actual.getNanos());
    }

    public void testSetUtcTimeOnlyField() throws Exception {
        FieldMap map = new Message();
        Timestamp aDate = new Timestamp(System.currentTimeMillis());
        map.setField(new UtcTimeOnlyField(MDEntryTime.FIELD, aDate, false, false));
        assertEquals("milliseconds should not be preserved", UtcTimeOnlyConverter.convert(aDate, false, false),
                    UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), false, false));

        // now set it with preserving millis
        map.setField(new UtcTimeOnlyField(MDEntryTime.FIELD, aDate, true, false));
        assertEquals("milliseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, true, false),
                    UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), true, false));
        
        // now set it with preserving micros
        map.setField(new UtcTimeOnlyField(MDEntryTime.FIELD, aDate, false, true));
        assertEquals("microseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, false, true),
                    UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), false, true));
        
        map.setField(new UtcTimeOnlyField(MDEntryTime.FIELD, aDate, true, true));
        assertEquals("microseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, true, true),
                    UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), true, true));
    }

    /**
     * Try a subclass of {@link UtcTimeOnlyField} and {@link UtcTimeStampField} directly
     */
    public void testSpecificFields() throws Exception {
        FieldMap map = new Message();
        Timestamp aDate = new Timestamp(System.currentTimeMillis());
        map.setField(new EffectiveTime(aDate));
        assertEquals("milliseconds should be preserved", aDate.getTime(),
                    map.getField(new EffectiveTime()).getValue().getTime());
        map.setField(new MDEntryTime(aDate));
        assertEquals("milliseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, true, false),
                    UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), true, false));
        
        assertEquals("microseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, false, true),
                UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), false, true));
        assertEquals("microseconds should be preserved", UtcTimeOnlyConverter.convert(aDate, true, true),
                UtcTimeOnlyConverter.convert(map.getField(new MDEntryTime()).getValue(), true, true));
    }

    private void testOrdering(int[] vals, int[] order, int[] expected) {
        FieldMap map = new Message(order);
        for (int v : vals)
            map.setInt(v, v);
        Iterator<Field<?>> it = map.iterator();
        for (int e : expected)
            assertEquals(String.valueOf(e), it.next().getObject());
    }

    public void testOrdering() {
        testOrdering(new int[] { 1, 2, 3 }, null, new int[] { 1, 2, 3 });
        testOrdering(new int[] { 3, 2, 1 }, null, new int[] { 1, 2, 3 });
        testOrdering(new int[] { 1, 2, 3 }, new int[] { 1, 2, 3 }, new int[] { 1, 2, 3 });
        testOrdering(new int[] { 3, 2, 1 }, new int[] { 1, 2, 3 }, new int[] { 1, 2, 3 });
        testOrdering(new int[] { 1, 2, 3 }, new int[] { 1, 3, 2 }, new int[] { 1, 3, 2 });
        testOrdering(new int[] { 3, 2, 1 }, new int[] { 1, 3, 2 }, new int[] { 1, 3, 2 });
        testOrdering(new int[] { 1, 2, 3 }, new int[] { 1, 3 }, new int[] { 1, 3, 2 });
        testOrdering(new int[] { 3, 2, 1 }, new int[] { 1, 3 }, new int[] { 1, 3, 2 });
        testOrdering(new int[] { 1, 2, 3 }, new int[] { 3, 1 }, new int[] { 3, 1, 2 });
        testOrdering(new int[] { 3, 2, 1 }, new int[] { 3, 1 }, new int[] { 3, 1, 2 });
    }
}
