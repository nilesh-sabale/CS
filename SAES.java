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

    // XOR of two binary strings
    static String xor(String a, String b) {
        int res = Integer.parseInt(a, 2) ^ Integer.parseInt(b, 2);
        return String.format("%" + a.length() + "s", Integer.toBinaryString(res)).replace(' ', '0');
    }

    // Substitute using S-Box
    static String subNib(String in) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < in.length(); i += 4) {
            int nibble = Integer.parseInt(in.substring(i, i + 4), 2);
            out.append(String.format("%4s", Integer.toBinaryString(SBox[nibble])).replace(' ', '0'));
        }
        return out.toString();
    }

    // Substitute using Inverse S-Box
    static String invSubNib(String in) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < in.length(); i += 4) {
            int nibble = Integer.parseInt(in.substring(i, i + 4), 2);
            out.append(String.format("%4s", Integer.toBinaryString(InvSBox[nibble])).replace(' ', '0'));
        }
        return out.toString();
    }

    // Rotate nibbles
    static String rotNib(String in) {
        return in.substring(4) + in.substring(0, 4);
    }

    // Key expansion: K0, K1, K2
    static String[] keyExpansion(String key) {
        String w0 = key.substring(0, 8);
        String w1 = key.substring(8);

        String rcon1 = "10000000";
        String rcon2 = "00110000";

        String temp1 = rotNib(w1);
        String temp2 = subNib(temp1);
        String w2 = xor(w0, xor(temp2, rcon1));
        String w3 = xor(w2, w1);

        String temp3 = rotNib(w3);
        String temp4 = subNib(temp3);
        String w4 = xor(w2, xor(temp4, rcon2));
        String w5 = xor(w4, w3);

        String k0 = w0 + w1;
        String k1 = w2 + w3;
        String k2 = w4 + w5;

        return new String[]{k0, k1, k2};
    }

    // GF(2^4) multiplication
    static int mul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) != 0)
                p ^= a;
            boolean hiBitSet = (a & 0x8) != 0;
            a <<= 1;
            if (hiBitSet)
                a ^= 0x13;  // x^4 + x + 1 = 0b10011
            b >>= 1;
        }
        return p & 0xF;
    }

    // MixColumns
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

        StringBuilder out = new StringBuilder();
        for (int val : r) {
            out.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }

        return out.toString();
    }

    // Encrypt function
    static String encrypt(String pt, String[] keys) {
        String state = xor(pt, keys[0]);     // AddRoundKey K0
        state = subNib(state);               // SubNib
        state = mixColumns(state);           // MixColumns
        state = xor(state, keys[1]);         // AddRoundKey K1
        state = subNib(state);               // SubNib
        state = xor(state, keys[2]);         // AddRoundKey K2
        return state;
    }

    // Decrypt function
    static String decrypt(String ct, String[] keys) {
        String state = xor(ct, keys[2]);         // Undo K2
        state = invSubNib(state);                // Inverse SubNib
        state = xor(state, keys[1]);             // Undo K1
        state = mixColumns(state);               // Forward MixColumns (used as inverse in S-AES)
        state = invSubNib(state);                // Inverse SubNib
        state = xor(state, keys[0]);             // Undo K0
        return state;
    }

    // Format binary string in 4-bit chunks
    static String formatNibble(String bin) {
        return bin.replaceAll("(.{4})", "$1 ").trim();
    }

    // Main function
    public static void main(String[] args) {
        String[] keys = keyExpansion(key);
        String ct = encrypt(plaintext, keys);
        String pt_decrypted = decrypt(ct, keys);

        System.out.println("Plaintext   : " + formatNibble(plaintext));
        System.out.println("Key         : " + formatNibble(key));
        System.out.println("Round Key K0: " + formatNibble(keys[0]));
        System.out.println("Round Key K1: " + formatNibble(keys[1]));
        System.out.println("Round Key K2: " + formatNibble(keys[2]));
        System.out.println("Ciphertext  : " + formatNibble(ct));
        System.out.println("Decrypted   : " + formatNibble(pt_decrypted));
    }
}
