public class SAES {
    static String key = "0100101011110101";        // 16-bit key
    static String plaintext = "1101011100101000";   // 16-bit plaintext

    static int[] SBox = {
        0x9, 0x4, 0xA, 0xB,
        0xD, 0x1, 0x8, 0x5,
        0x6, 0x2, 0x0, 0x3,
        0xC, 0xE, 0xF, 0x7
    };

    static int[] InvSBox = {
        0xA, 0x5, 0x9, 0xB,
        0x1, 0x7, 0x8, 0xF,
        0x6, 0x0, 0x2, 0x3,
        0xC, 0x4, 0xD, 0xE
    };

    static String xor(String a, String b) {
        int res = Integer.parseInt(a, 2) ^ Integer.parseInt(b, 2);
        return String.format("%" + a.length() + "s", Integer.toBinaryString(res)).replace(' ', '0');
    }

    static String subNib(String in) {
        String out = "";
        for (int i = 0; i < 2; i++) {
            int nibble = Integer.parseInt(in.substring(i * 4, (i + 1) * 4), 2);
            out += String.format("%4s", Integer.toBinaryString(SBox[nibble])).replace(' ', '0');
        }
        return out;
    }

    static String invSubNib(String in) {
        String out = "";
        for (int i = 0; i < 4; i++) {
            int nibble = Integer.parseInt(in.substring(4 * i, 4 * (i + 1)), 2);
            out += String.format("%4s", Integer.toBinaryString(InvSBox[nibble])).replace(' ', '0');
        }
        return out;
    }

    static String rotNib(String in) {
        return in.substring(4) + in.substring(0, 4);
    }

    static String[] keyExpansion(String key) {
        String w0 = key.substring(0, 8);  // 01001010
        String w1 = key.substring(8);     // 11110101

        // round constant 1
        String rcon1 = "10000000";
        String rcon2 = "00110000";

        // w2 = w0 XOR SubNib(RotNib(w1)) XOR Rcon1
        String temp1 = rotNib(w1);                 // rotate w1
        String temp2 = subNib(temp1);              // apply sbox
        String w2 = xor(w0, xor(temp2, rcon1));

        // w3 = w2 XOR w1
        String w3 = xor(w2, w1);

        // w4 = w2 XOR SubNib(RotNib(w3)) XOR Rcon2
        String temp3 = rotNib(w3);
        String temp4 = subNib(temp3);
        String w4 = xor(w2, xor(temp4, rcon2));

        // w5 = w4 XOR w3
        String w5 = xor(w4, w3);

        String k0 = w0 + w1;
        String k1 = w2 + w3;
        String k2 = w4 + w5;

        return new String[]{k0, k1, k2};
    }

    static int mul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) != 0)
                p ^= a;
            boolean hi_bit_set = (a & 0x8) != 0;
            a <<= 1;
            if (hi_bit_set)
                a ^= 0x13;
            b >>= 1;
        }
        return p & 0xF;
    }

    static String mixColumns(String in) {
        int[] m = new int[4];
        for (int i = 0; i < 4; i++) {
            m[i] = Integer.parseInt(in.substring(i * 4, (i + 1) * 4), 2);
        }

        int[] r = new int[4];
        r[0] = m[0] ^ mul(4, m[2]);
        r[1] = m[1] ^ mul(4, m[3]);
        r[2] = m[2] ^ mul(4, m[0]);
        r[3] = m[3] ^ mul(4, m[1]);

        String out = "";
        for (int val : r) {
            out += String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0');
        }

        return out;
    }

    static String encrypt(String pt, String[] keys) {
        String state = xor(pt, keys[0]);
        state = subNib(state);
        state = mixColumns(state);
        state = xor(state, keys[1]);
        state = subNib(state);
        state = xor(state, keys[2]);
        return state;
    }

    static String decrypt(String ct, String[] keys) {
        String state = xor(ct, keys[2]);
        state = invSubNib(state);
        state = xor(state, keys[1]);
        state = mixColumns(state);
        state = invSubNib(state);
        state = xor(state, keys[0]);
        return state;
    }

    public static void main(String[] args) {
        String[] keys = keyExpansion(key);

        System.out.println("Plaintext   : " + plaintext);
        System.out.println("Key         : " + key);
        System.out.println("Round Key K0: " + keys[0] + " = " + formatNibble(keys[0]));
        System.out.println("Round Key K1: " + keys[1] + " = " + formatNibble(keys[1]));
        System.out.println("Round Key K2: " + keys[2] + " = " + formatNibble(keys[2]));

        String ct = encrypt(plaintext, keys);
        System.out.println("Ciphertext  : " + ct);

        String decrypted = decrypt(ct, keys);
        System.out.println("Decrypted   : " + decrypted);
    }

    static String formatNibble(String bin) {
        return bin.replaceAll("(.{4})", "$1 ").trim();
    }
}
