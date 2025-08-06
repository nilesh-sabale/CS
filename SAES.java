import java.util.Scanner;

public class SAES {

    static final int[] SBox = {
        0x9, 0x4, 0xA, 0xB,
        0xD, 0x1, 0x8, 0x5,
        0x6, 0x2, 0x0, 0x3,
        0xC, 0xE, 0xF, 0x7
    };

    static final int[] InvSBox = {
        0xA, 0x5, 0x9, 0xB,
        0x1, 0x7, 0x8, 0xF,
        0x6, 0x0, 0x2, 0x3,
        0xC, 0x4, 0xD, 0xE
    };

    static String xor(String a, String b) {
        int res = Integer.parseInt(a, 2) ^ Integer.parseInt(b, 2);
        return String.format("%" + a.length() + "s", Integer.toBinaryString(res)).replace(' ', '0');
    }

    static String subNib(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i += 4) {
            int nibble = Integer.parseInt(input.substring(i, i + 4), 2);
            result.append(String.format("%4s", Integer.toBinaryString(SBox[nibble])).replace(' ', '0'));
        }
        return result.toString();
    }

    static String invSubNib(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i += 4) {
            int nibble = Integer.parseInt(input.substring(i, i + 4), 2);
            result.append(String.format("%4s", Integer.toBinaryString(InvSBox[nibble])).replace(' ', '0'));
        }
        return result.toString();
    }

    static String rotNib(String input) {
        return input.substring(4) + input.substring(0, 4);
    }

    static String[] keyExpansion(String key) {
        String w0 = key.substring(0, 8);
        String w1 = key.substring(8);
        String rcon1 = "10000000";
        String rcon2 = "00110000";

        String temp = subNib(rotNib(w1));
        String w2 = xor(w0, xor(temp, rcon1));
        String w3 = xor(w2, w1);

        temp = subNib(rotNib(w3));
        String w4 = xor(w2, xor(temp, rcon2));
        String w5 = xor(w4, w3);

        return new String[]{w0 + w1, w2 + w3, w4 + w5};
    }

    static int mul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) == 1) p ^= a;
            boolean highBit = (a & 0x8) != 0;
            a <<= 1;
            if (highBit) a ^= 0x13;
            b >>= 1;
        }
        return p & 0xF;
    }

    static String mixColumns(String input) {
        int[] s = new int[4];
        for (int i = 0; i < 4; i++) {
            s[i] = Integer.parseInt(input.substring(i * 4, (i + 1) * 4), 2);
        }

        int[] r = new int[4];
        r[0] = s[0] ^ mul(4, s[2]);
        r[1] = s[1] ^ mul(4, s[3]);
        r[2] = s[2] ^ mul(4, s[0]);
        r[3] = s[3] ^ mul(4, s[1]);

        StringBuilder out = new StringBuilder();
        for (int val : r) {
            out.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }

        return out.toString();
    }

    static String invMixColumns(String input) {
        int[] s = new int[4];
        for (int i = 0; i < 4; i++) {
            s[i] = Integer.parseInt(input.substring(i * 4, (i + 1) * 4), 2);
        }

        int[] r = new int[4];
        r[0] = mul(9, s[0]) ^ mul(2, s[2]);
        r[1] = mul(9, s[1]) ^ mul(2, s[3]);
        r[2] = mul(9, s[2]) ^ mul(2, s[0]);
        r[3] = mul(9, s[3]) ^ mul(2, s[1]);

        StringBuilder out = new StringBuilder();
        for (int val : r) {
            out.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }

        return out.toString();
    }

    static String encrypt(String pt, String[] keys) {
        String state = xor(pt, keys[0]);
        state = subNib(state);
        state = mixColumns(state);
        state = xor(state, keys[1]);
        state = subNib(state);
        return xor(state, keys[2]);
    }

    static String decrypt(String ct, String[] keys) {
        String state = xor(ct, keys[2]);
        state = invSubNib(state);
        state = xor(state, keys[1]);
        state = invMixColumns(state);
        state = invSubNib(state);
        return xor(state, keys[0]);
    }

    static String formatBin(String bin) {
        return bin.replaceAll("(.{4})", "$1 ").trim();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter 16-bit key: ");
        String key = sc.next();
        while (key.length() != 16 || !key.matches("[01]+")) {
            System.out.print("Invalid input. Enter exactly 16-bit binary key: ");
            key = sc.next();
        }

        System.out.print("Enter 16-bit plaintext: ");
        String plaintext = sc.next();
        while (plaintext.length() != 16 || !plaintext.matches("[01]+")) {
            System.out.print("Invalid input. Enter exactly 16-bit binary plaintext: ");
            plaintext = sc.next();
        }

        String[] keys = keyExpansion(key);

        String ciphertext = encrypt(plaintext, keys);
        String decrypted = decrypt(ciphertext, keys);

        System.out.println("\n====== Simplified AES Result ======");
        System.out.println("Plaintext   : " + formatBin(plaintext));
        System.out.println("Key         : " + formatBin(key));
        System.out.println("Round Key K0: " + formatBin(keys[0]));
        System.out.println("Round Key K1: " + formatBin(keys[1]));
        System.out.println("Round Key K2: " + formatBin(keys[2]));
        System.out.println("Ciphertext  : " + formatBin(ciphertext));
        System.out.println("Decrypted   : " + formatBin(decrypted));

        sc.close();
    }
}
