import java.util.Random;

/**
 * This is a toy block cipher. It's not actually secure :)
 */
public class Main {
    public static void main(String[] args) {
        // TODO
        String key = "1100001 0110101 1011010 0100011 0001001";
        String plaintext = "HelloHello~";
        String initializationVector =
            groupsOfSeven(randomInitializationVector(35));
            // "0000000 0000000 0000000 0000000 0000000";


        System.out.println("Plaintext: " + plaintext);
        System.out.println("Initialization vector: " + initializationVector);
        System.out.println("");

        System.out.println("=== Electronic Code Book mode ===");
        String ecbCiphertext = groupsOfSeven(encryptEcb(plaintext, key));
        System.out.println("Encrypted: " + ecbCiphertext);
        String ecbPlaintext = decryptEcb(ecbCiphertext, key);
        System.out.println("Decrypted: " + ecbPlaintext);
        System.out.println("");

        System.out.println("=== Cipher Block Chaining mode ===");
        String cbcCiphertext =
          groupsOfSeven(encryptCbc(plaintext, key, initializationVector));
        System.out.println("Encrypted: " + cbcCiphertext);
        String cbcPlaintext =
          decryptCbc(cbcCiphertext, key, initializationVector);
        System.out.println("Decrypted: " + cbcPlaintext);
        System.out.println("");
    }

    /**
     * Encrypt a plaintext using our toy algorithm in the ECB (Electronic Code
     * Book) mode.
     *
     * The plaintext should be a string consisting only of ASCII characters;
     * the key should be a string of 35 '0' and '1' characters (that is, the
     * textual representation of a 35-digit binary number.)
     *
     * Spaces in the key string will be ignored.
     */
    public static String encryptEcb(String plaintext, String key) {
        key = key.replaceAll("\\s", "");

        // Each block is a group of 5 characters.
        // (5 characters times 7 bits per character makes 35 bits).
        int numberOfCompleteBlocks = plaintext.length() / 5;
        boolean isThereAnIncompleteBlock = plaintext.length() % 5 != 0;

        // Using a StringBuilder here would be faster and probably better
        // practice, but I'm not particularly concerned with performance right
        // now :-)
        String ciphertext = "";

        for (int i = 0; i < numberOfCompleteBlocks; i++) {
            String plaintextBlock = plaintext.substring(5 * i, 5 * (i + 1));
            ciphertext += encryptBlock(plaintextBlock, key);
        }

        if (isThereAnIncompleteBlock) {
            String incompleteBlock =
                plaintext.substring(5 * numberOfCompleteBlocks, plaintext.length());
            // Pad with spaces on the right until the string has length five,
            // and then replace those spaces with null characters.
            String paddedBlock =
                String.format("%-5s", incompleteBlock).replace(" ", "\0");

            ciphertext += encryptBlock(paddedBlock, key);
        }

        return ciphertext;
    }

    /**
     * Decrypt a ciphertext using our toy algorithm in the ECB (Electronic Code
     * Book) mode.
     *
     * The ciphertext should be a string consisting of the characters '0' and
     * '1'. (That is, it's a textual representation of a stream of bits.)
     *
     * As always, the key should be a string consisting of '1's and '0's.
     *
     * Any whitespace characters in both the ciphertext and key will be
     * ignored.
     *
     * The key should be 35 bits long, and the length of the ciphertext should
     * be a multiple of 35. (Ignoring whitespace, of course.)
     */
    public static String decryptEcb(String ciphertext, String key) {
        key = key.replaceAll("\\s", "");
        ciphertext = ciphertext.replaceAll("\\s", "");

        int numberOfBlocks = ciphertext.length() / 35;

        String plaintext = "";

        for (int i = 0; i < numberOfBlocks; i++) {
            String block = ciphertext.substring(35 * i, 35 * (i + 1));
            plaintext += decryptBlock(block, key);
        }

        return plaintext;
    }

    /**
     * Like encryptEcb, but using CBC (cipher block chaining) mode.
     *
     * The plaintext should be a string of ASCII characters.
     *
     * The key and initialization vector should be strings of 0 and 1.
     */
    public static String encryptCbc(String plaintext, String key, String initializationVector) {
        key = key.replaceAll("\\s", "");
        initializationVector = initializationVector.replaceAll("\\s", "");

        int numberOfCompleteBlocks = plaintext.length() / 5;
        boolean isThereAnIncompleteBlock = plaintext.length() % 5 != 0;

        String xorString = initializationVector;
        String ciphertext = "";

        for(int i=0; i<numberOfCompleteBlocks; i++) {
            String plaintextBlock = plaintext.substring(5 * i, 5 * (i + 1));
            plaintextBlock = xorAscii(plaintextBlock, stringOfBitsToAscii(xorString));
            plaintextBlock = encryptBlock(plaintextBlock, key);

            ciphertext += plaintextBlock;
            xorString = plaintextBlock;
        }

        if (isThereAnIncompleteBlock) {
            String incompleteBlock =
                plaintext.substring(5 * numberOfCompleteBlocks, plaintext.length());
            // Pad with spaces on the right until the string has length five,
            // and then replace those spaces with null characters.
            String paddedBlock =
                String.format("%-5s", incompleteBlock).replace(" ", "\0");

            paddedBlock = xorAscii(paddedBlock, stringOfBitsToAscii(xorString));
            paddedBlock = encryptBlock(paddedBlock, key);

            ciphertext += paddedBlock;
        }

        return ciphertext;
    }

    /**
     * Like decryptEcb, but using CBC (cipher block chaining) mode.
     *
     * The ciphertext, key, and initialization vector should all be strings of
     * 0 and 1.
     */
    public static String decryptCbc(
            String ciphertext,
            String key,
            String initializationVector) {
        key = key.replaceAll("\\s", "");
        ciphertext = ciphertext.replaceAll("\\s", "");
        initializationVector = initializationVector.replaceAll("\\s", "");

        int numberOfBlocks = ciphertext.length() / 35;

        String plaintext = "";

        String xorBits = initializationVector;
        for(int i = 0; i < numberOfBlocks; i++) {
            String ciphertextBlock = ciphertext.substring(35 * i, 35 * (i + 1));
            plaintext +=
                xorAscii(
                    decryptBlock(ciphertextBlock, key),
                    stringOfBitsToAscii(xorBits));

            // Keep track of the previous ciphertext block.
            xorBits = ciphertextBlock;
        }

        return plaintext;
    }




    /**
     * Encrypt a block of plaintext.
     *
     * @param plaintext Five characters of ASCII to encrypt.
     *
     * @param key A string consisting solely of the characters '1' and '0', 35
     * characters long. (That is, the string representation of a binary
     * number.) The secret symmetric key for this algorithm.
     */
    private static String encryptBlock(String plaintext, String key) {
        String plaintextBinary = asciiToStringOfBits(plaintext);

        // Shift right by 3
        String plaintextAfterShift =
          plaintextBinary.substring(plaintextBinary.length() - 3, plaintextBinary.length())
          + plaintextBinary.substring(0, plaintextBinary.length() - 3);

        String encryptedBlock = xor(plaintextAfterShift, key);
        return encryptedBlock;
    }

    private static String decryptBlock(String encryptedText, String key) {
        String plaintextAfterShift = xor(encryptedText, key);

        String plaintextBinary =
            plaintextAfterShift.substring(3, plaintextAfterShift.length())
            + plaintextAfterShift.substring(0, 3);


        String decryptedBlock = stringOfBitsToAscii(plaintextBinary);

        return decryptedBlock;
    }

    /**
     * Perform a bitwise xor operation on two bitstrings.
     *
     * (Each bitstring is represented as a string consisting only of the
     * characters '0' and '1', so that each character represents one bit.)
     *
     * As a precondition, a and b should have the same length.
     */
    private static String xor(String a, String b){
        String xorString = "";

        for(int i=0; i < a.length(); i++){
            if(a.charAt(i) == b.charAt(i)) { xorString += "0"; }
            else { xorString += "1"; }
        }

        return xorString;
    }

    /**
     * Like xor, but the strings and output consist arbitrary ASCII characters,
     * with each character representing seven bits.
     *
     * As a precondition, a an b should have the same length.
     */
    private static String xorAscii(String a, String b) {
        String result = "";
        for (int i = 0; i < a.length(); i++) {
            result += (char)(a.charAt(i) ^ b.charAt(i));
        }
        return result;
    }


    /**
     * Given a string, add a single space in-between groups of seven
     * characters.
     *
     * (This is helpful for visualizing the encryption process, since our toy
     * algorithm is designed around groups of seven bits.)
     */
    private static String groupsOfSeven(String input) {
        String result = "";
        for (int i = 0; i < input.length(); i++) {
            result += input.charAt(i);

            // Add a space after you've read seven characters, but never add a
            // space after the last character.
            if ((i + 1) % 7 == 0 && i != input.length() - 1) {
                result += " ";
            }
        }

        return result;
    }

    /**
     * Make a string consisting only of the characters '1' and '0' (that is,
     * the text representation of a binary number.) Each character is randomly
     * chosen to be either 1 or 0.
     *
     * (Since this is a toy example, we aren't using a
     * cryptographically-secure pseudorandom number generator for this method,
     * but in the real world you would want to use something less predictable
     * than `java.util.Random`.)
     */
    private static String randomInitializationVector(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            // Add one random bit to the end of the string.
            result += (new Random()).nextBoolean() ? "1" : "0";
        }
        return result;
    }

    /**
     * Given a string of ASCII characters, turn each ASCII character into
     * a seven-bit binary number (big-endian, so that the most significant
     * bit is on the left).
     *
     * Pad each character with zeros so that it's seven bits exactly.
     */
    public static String asciiToStringOfBits(String ascii) {
        String binary = "";
        for (int i = 0; i < ascii.length(); i++) {
            // Since, as a precondition, string only has ASCII characters,
            // this string will have length at most seven.
            String binaryDigits = Integer.toBinaryString(ascii.charAt(i));

            // Pad with zeros on the left until the string is seven digits
            // long.
            // (This part is a bit messy 🙃)
            String binaryDigitsPadded =
                String.format("%7s", binaryDigits).replace(" ", "0");
            binary += binaryDigitsPadded;
        }
        return binary;
    }

    /**
     * Given a string of '0' and '1' characters, whose length is a multiple of
     * seven, turn each group of seven '0's and '1's into the corresponding
     * ASCII character into a seven-bit binary number (big-endian, so that the
     * most significant bit is on the left).
     */
    public static String stringOfBitsToAscii(String bits) {
        String ascii = "";

        // Because of our precondition that the length of `bits` is a multiple
        // of seven, it should divide evenly here.
        String[] groupsOfSeven = groupsOfSeven(bits).split("\\s");

        for (int i = 0; i < groupsOfSeven.length; i++){
            ascii += (char)Integer.parseInt(groupsOfSeven[i], 2);
        }

        return ascii;
    }

}