import java.math.BigInteger;
import java.util.Scanner;

public class RSAWithBigInteger {
    
    static int gcd(int a, int b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    static int modInverse(int e, int phi) {
        for (int d = 1; d < phi; d++) {
            if ((d * e) % phi == 1)
                return d;
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter prime number p: ");
        int p = sc.nextInt();
        System.out.print("Enter prime number q: ");
        int q = sc.nextInt();

        int n = p * q;
        int phi = (p - 1) * (q - 1);

        int e;
        while (true) {
            System.out.print("Enter public exponent e (1 < e < " + phi + "): ");
            e = sc.nextInt();
            if (e > 1 && e < phi && gcd(e, phi) == 1)
                break;
            System.out.println("Invalid e. Try again.");
        }

        int d = modInverse(e, phi);
        if (d == -1) {
            System.out.println("Modular inverse not found.");
            return;
        }

        System.out.println("\nPublic Key: (" + e + ", " + n + ")");
        System.out.println("Private Key: (" + d + ", " + n + ")");

        System.out.print("\nEnter message (number < " + n + "): ");
        int msg = sc.nextInt();

        BigInteger M = BigInteger.valueOf(msg);
        BigInteger E = BigInteger.valueOf(e);
        BigInteger D = BigInteger.valueOf(d);
        BigInteger N = BigInteger.valueOf(n);

        BigInteger C = M.modPow(E, N);
        System.out.println("Encrypted Message: " + C);

        BigInteger decrypted = C.modPow(D, N);
        System.out.println("Decrypted Message: " + decrypted);
    }
}
