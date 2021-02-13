package com.github.francescociraolo.bcctrace;

/**
 * Simple BitArray implementation, here mainly for {@link BitArray#valuesFromIntString(String)}
 */
public class BitArray {

    private final int value;

    private BitArray(int array) {
        this.value = array;
    }

    BitArray not() {
        return not(this);
    }

    BitArray and(BitArray other) {
        return and(this, other);
    }

    BitArray or(BitArray other) {
        return or(this, other);
    }

    BitArray xor(BitArray other) {
        return xor(this, other);
    }


    public static BitArray parseFromInteger(int representation) {
        return new BitArray(representation);
    }

    public static BitArray parseFromIntegerString(String string) {
        var val = Integer.parseInt(string);

        return new BitArray(val);
    }

    public static int[] valuesFromIntString(String intString) {
        var val = Integer.parseInt(intString);
        var length = 0;
        var tmp = new int[Integer.SIZE];

        for (int i = 0; val > 0; i++, val >>= 1)
            if (val % 2 == 1)
                tmp[length++] = i;

        var res = new int[length];
        System.arraycopy(tmp, 0, res, 0, length);
        return res;
    }


    public static BitArray not(BitArray b) {
        return new BitArray(~b.value);
    }

    public static BitArray and(BitArray b0, BitArray b1) {
        return new BitArray(b0.value & b1.value);
    }

    public static BitArray or(BitArray b0, BitArray b1) {
        return new BitArray(b0.value | b1.value);
    }

    public static BitArray xor(BitArray b0, BitArray b1) {
        return new BitArray(b0.value ^ b1.value);
    }
}
