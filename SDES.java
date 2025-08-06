import java.util.Scanner;

public class SDES {

    static final int[] P10    = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    static final int[] P8     = {6, 3, 7, 4, 8, 5, 10, 9};
    static final int[] IP     = {2, 6, 3, 1, 4, 8, 5, 7};
    static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    static final int[] EP     = {4, 1, 2, 3, 2, 3, 4, 1};
    static final int[] P4     = {2, 4, 3, 1};

    static final int[][] S0 = {
        {1, 0, 3, 2},
        {3, 2, 1, 0},
        {0, 2, 1, 3},
        {3, 1, 3, 2}
    };

    static final int[][] S1 = {
        {0, 1, 2, 3},
        {2, 0, 1, 3},
        {3, 0, 1, 0},
        {2, 1, 0, 3}
    };

    static class Keys {
        String k1, k2;
        Keys(String k1, String k2) {
            this.k1 = k1;
            this.k2 = k2;
        }
    }

    static String permute(String input, int[] table) {
        StringBuilder result = new StringBuilder();
        for (int pos : table) {
            result.append(input.charAt(pos - 1));
        }
        return result.toString();
    }

    static String leftShift(String bits, int count) {
        return bits.substring(count) + bits.substring(0, count);
    }

    static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) ^ b.charAt(i));
        }
        return result.toString();
    }

    static String sBoxLookup(String input, int[][] sBox) {
        int row = Integer.parseInt("" + input.charAt(0) + input.charAt(3), 2);
        int col = Integer.parseInt("" + input.charAt(1) + input.charAt(2), 2);
        int val = sBox[row][col];
        return String.format("%2s", Integer.toBinaryString(val)).replace(' ', '0');
    }

    static Keys generateKeys(String key10) {
        String p10 = permute(key10, P10);
        String left = p10.substring(0, 5);
        String right = p10.substring(5);

        left = leftShift(left, 1);
        right = leftShift(right, 1);
        String k1 = permute(left + right, P8);

        left = leftShift(left, 2);
        right = leftShift(right, 2);
        String k2 = permute(left + right, P8);

        return new Keys(k1, k2);
    }

    static String fK(String left, String right, String subKey) {
        String expanded = permute(right, EP);
        String xorResult = xor(expanded, subKey);

        String leftPart = xorResult.substring(0, 4);
        String rightPart = xorResult.substring(4);

        String s0out = sBoxLookup(leftPart, S0);
        String s1out = sBoxLookup(rightPart, S1);

        String combined = s0out + s1out;
        String p4 = permute(combined, P4);

        return xor(left, p4);
    }

    static String encrypt(String plaintext, Keys keys) {
        String permuted = permute(plaintext, IP);
        String left = permuted.substring(0, 4);
        String right = permuted.substring(4);

        String round1 = fK(left, right, keys.k1);
        String round2 = fK(right, round1, keys.k2);

        String finalBits = round2 + round1;
        return permute(finalBits, IP_INV);
    }

    static String decrypt(String ciphertext, Keys keys) {
        String permuted = permute(ciphertext, IP);
        String left = permuted.substring(0, 4);
        String right = permuted.substring(4);

        String round1 = fK(left, right, keys.k2);
        String round2 = fK(right, round1, keys.k1);

        String finalBits = round2 + round1;
        return permute(finalBits, IP_INV);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter 10-bit key (binary): ");
        String key = sc.next();
        while (key.length() != 10 || !key.matches("[01]+")) {
            System.out.print("Invalid. Enter a 10-bit binary key: ");
            key = sc.next();
        }

        System.out.print("Enter 8-bit plaintext (binary): ");
        String plaintext = sc.next();
        while (plaintext.length() != 8 || !plaintext.matches("[01]+")) {
            System.out.print("Invalid. Enter an 8-bit binary plaintext: ");
            plaintext = sc.next();
        }

        Keys keys = generateKeys(key);
        String encrypted = encrypt(plaintext, keys);
        String decrypted = decrypt(encrypted, keys);

        System.out.println("\n===== S-DES Result =====");
        System.out.println("Subkey 1     : " + keys.k1);
        System.out.println("Subkey 2     : " + keys.k2);
        System.out.println("Plaintext    : " + plaintext);
        System.out.println("Encrypted    : " + encrypted);
        System.out.println("Decrypted    : " + decrypted);

        sc.close();
    }
}
