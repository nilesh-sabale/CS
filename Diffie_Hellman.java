import java.util.Scanner;

public class Diffie_Hellman {
    public static void main(String[] args) {
        long p,g,a,b,x,y;
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter P and G :");
        p=sc.nextLong();
        g=sc.nextLong();  //produces all numbers from 1 to p−1 (in any order).

        System.out.println("Enter Alice private key a:");
        a=sc.nextLong();
        System.out.println("Enter Bob private key b:");
        b=sc.nextLong();

        //Alice and bob compute public values
        /*
         * x=g^a%p; y=g^b%p;
         */
        x=power(g,a,p);
        System.out.println("Alice recieves x:"+x);
        y=power(g, b, p);
        System.out.println("Bob recieves y:"+y);
        System.out.println("Now Exchanged public values of a,b");

        //computer shared key
        long k1=power(y, a, p);
        System.out.println("alice computed :"+k1);

        long k2=power(x, b, p);
        System.out.println("bob computed :"+k2);

        if(k1==k2){
            System.out.println("Shared secret key:"+k1);
        }else{
            System.out.println("Error in diff hillman");
        }
    }
    // Power function to return value of a ^ b mod P
    //                                   g   a     p  
    public static long power(long g,long a,long p){
        if(a==1){
            return g;
        }else {
            return (long)Math.pow(g, a)%p;
        }
    }
    
}

/*
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
*/