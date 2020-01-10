package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.util.UnsafeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LocalBufferTest {

    private static final long CAPACITY = 1024;
    private LocalBuffer buffer;

    @BeforeEach
    public void setupBuffer() {
        buffer = LocalBuffer.allocate(CAPACITY);
    }

    @Test
    void testBoundsCheck() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> buffer.get(CAPACITY));

        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> buffer.getShort(CAPACITY - Byte.BYTES));

        assertThatCode(() -> buffer.get(CAPACITY - 1))
                .doesNotThrowAnyException();
    }

    @Test
    void offHeapObjectTest() {
        var unsafe = UnsafeProvider.getUnsafe();
        var object = new SimpleObject(8, 20);

        System.out.printf("0x%016X\n", unsafe.getInt(object, 0));
        System.out.printf("0x%016X\n", unsafe.getLong(object, 4));
        System.out.printf("x value: %d\n", unsafe.getInt(object, 12));
        System.out.printf("y value: %d\n", unsafe.getInt(object, 16));
    }

    public static long sizeOf(Object object) {
        var unsafe = UnsafeProvider.getUnsafe();
        return unsafe.getAddress( normalize( unsafe.getInt(object, 4L) ) + 12L );
    }

    public static long normalize(int value) {
        if(value >= 0) return value;
        return (~0L >>> 32) & value;
    }

    private static final class SimpleObject {
        private int x;
        private int y;

        public SimpleObject(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}