public class diffieHellman {

    //Simple method for (base^exp) % mod
    public static int modPow(int base, int exp, int mod) {
        int result = 1;
        for (int i = 0; i < exp; i++) {
            result = (result * base) % mod;
        }
        return result;
    }

    public static void main(String[] args) {
        int p = 17;
        int g = 3;
        int a = 5;
        int b = 7;

        int A = modPow(g, a, p); 
        int B = modPow(g, b, p); 

        int secretA = modPow(B, a, p);
        int secretB = modPow(A, b, p);

        System.out.println("Public p: " + p);
        System.out.println("Primitive g: " + g);
        System.out.println("Alice public key: " + A);
        System.out.println("Bob public key: " + B);
        System.out.println("Shared key for Alice: " + secretA);
        System.out.println("Shared key for Bob: " + secretB);
    }
}
