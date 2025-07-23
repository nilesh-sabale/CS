import java.util.Scanner;

public class SDES {

    static int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    static int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    static int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    static int[] IPinv = {4, 1, 3, 5, 7, 2, 8, 6};
    static int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    static int[] P4 = {2, 4, 3, 1};

    static int[][] S0 = {
        {1, 0, 3, 2},
        {3, 2, 1, 0},
        {0, 2, 1, 3},
        {3, 1, 3, 2}
    };

    static int[][] S1 = {
        {0, 1, 2, 3},
        {2, 0, 1, 3},
        {3, 0, 1, 0},
        {2, 1, 0, 3}
    };

    // Class to hold both keys
    static class Keys {
        String k1;
        String k2;

        Keys(String k1, String k2) {
            this.k1 = k1;
            this.k2 = k2;
        }
    }

    static String permute(String input, int[] table) {
        StringBuilder output = new StringBuilder();
        for (int i : table) output.append(input.charAt(i - 1));
        return output.toString();
    }

    static String leftShift(String input, int shifts) {
        return input.substring(shifts) + input.substring(0, shifts);
    }

    static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++)
            result.append(a.charAt(i) ^ b.charAt(i));
        return result.toString();
    }

    static String sBox(String input, int[][] sbox) {
        int row = Integer.parseInt("" + input.charAt(0) + input.charAt(3), 2);
        int col = Integer.parseInt("" + input.charAt(1) + input.charAt(2), 2);
        int val = sbox[row][col];
        return String.format("%2s", Integer.toBinaryString(val)).replace(' ', '0');
    }

    static Keys generateKeys(String key) {
        String p10 = permute(key, P10);
        String left = p10.substring(0, 5);
        String right = p10.substring(5);

        // Left shift 1
        left = leftShift(left, 1);
        right = leftShift(right, 1);
        String k1 = permute(left + right, P8);

        // Left shift 2
        left = leftShift(left, 2);
        right = leftShift(right, 2);
        String k2 = permute(left + right, P8);

        return new Keys(k1, k2);
    }

    static String fK(String L, String R, String key) {
        String ep = permute(R, EP);
        String xorResult = xor(ep, key);
        String left = xorResult.substring(0, 4);
        String right = xorResult.substring(4, 8);
        String sboxOutput = sBox(left, S0) + sBox(right, S1);
        String p4 = permute(sboxOutput, P4);
        return xor(L, p4);
    }

    static String encrypt(String plaintext, Keys keys) {
        String ip = permute(plaintext, IP);
        String l = ip.substring(0, 4);
        String r = ip.substring(4);

        // Round 1 with K1
        String temp = fK(l, r, keys.k1);
        // Swap
        l = r;
        r = temp;

        // Round 2 with K2
        String result = fK(l, r, keys.k2);

        String preoutput = result + temp;
        return permute(preoutput, IPinv);
    }

    static String decrypt(String ciphertext, Keys keys) {
        String ip = permute(ciphertext, IP);
        String l = ip.substring(0, 4);
        String r = ip.substring(4);

        // Round 1 with K2
        String temp = fK(l, r, keys.k2);
        // Swap
        l = r;
        r = temp;

        // Round 2 with K1
        String result = fK(l, r, keys.k1);

        String preoutput = result + temp;
        return permute(preoutput, IPinv);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 🔐 User input
        System.out.print("Enter 10-bit key (e.g., 1010000010): ");
        String key = sc.next();
        while (key.length() != 10 || !key.matches("[01]+")) {
            System.out.print("Invalid input. Enter exactly 10-bit binary key: ");
            key = sc.next();
        }

        System.out.print("Enter 8-bit plaintext (e.g., 10010111): ");
        String plaintext = sc.next();
        while (plaintext.length() != 8 || !plaintext.matches("[01]+")) {
            System.out.print("Invalid input. Enter exactly 8-bit binary plaintext: ");
            plaintext = sc.next();
        }

        // 🔐 Generate keys
        Keys keys = generateKeys(key);

        // 🔒 Encrypt & 🔓 Decrypt
        String encrypted = encrypt(plaintext, keys);
        String decrypted = decrypt(encrypted, keys);

        // 🖨️ Output
        System.out.println("\n=== S-DES Result ===");
        System.out.println("Key1      : " + keys.k1);
        System.out.println("Key2      : " + keys.k2);
        System.out.println("Plaintext : " + plaintext);
        System.out.println("Encrypted : " + encrypted);
        System.out.println("Decrypted : " + decrypted);

        sc.close();
    }
}
