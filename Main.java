import java.util.Arrays;

/**
 * This is a toy block cipher. It's not actually secure :)
 */
public class Main {
    public static void main(String[] args) {
        // TODO
        String key = "1100001 0110101 1011010 0100011 0001001";

        String plaintext = "HelloHello~";
        System.out.println("Plaintext: " + plaintext);

        String ciphertext = groupsOfSeven(encryptEcb(plaintext, key));
        System.out.println("Encrypted: " + ciphertext);

        String plaintextAgain = decryptEcb(ciphertext, key);
        System.out.println("Decrypted: " + plaintextAgain);
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
            // Pad with spaces on the right until the string has length seven,
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
     * Encrypt a block of plaintext.
     *
     * @param plaintext Five characters of ASCII to encrypt.
     *
     * @param key A string consisting solely of the characters '1' and '0', 35
     * characters long. (That is, the string representation of a binary
     * number.) The secret symmetric key for this algorithm.
     */
    private static String encryptBlock(String plaintext, String key) {
        String plaintextBinary = "";

        for (int i = 0; i < plaintext.length(); i++) {
            // Since, as a precondition, plaintext only has ASCII characters,
            // this string will have length at most seven.
            String binaryDigits = Integer.toBinaryString(plaintext.charAt(i));

            // Pad with zeros on the left until the string is seven digits
            // long.
            // (This part is a bit messy 🙃)
            String binaryDigitsPadded =
                String.format("%7s", binaryDigits).replace(" ", "0");
            plaintextBinary += binaryDigitsPadded;
        }

        // Shift right by 3
        String plaintextAfterShift =
          plaintextBinary.substring(plaintextBinary.length() - 3, plaintextBinary.length())
          + plaintextBinary.substring(0, plaintextBinary.length() - 3);

        String encryptedBlock = xor(plaintextAfterShift, key);
        return encryptedBlock;
    }

    private static String decryptBlock(String encryptedText, String key) {
        String plaintextAfterShift = xor(encryptedText, key);

        String plaintextBinary = plaintextAfterShift.substring(3, plaintextAfterShift.length())
        + plaintextAfterShift.substring(0, 3);

        String[] binaryStrings = new String[5];
        String tempString = "";
        for(int i=0; i<plaintextBinary.length(); i++){
            tempString += plaintextBinary.charAt(i);
            if((i+1)%7 == 0){
                binaryStrings[((i+1)/7) - 1] = tempString;
                tempString = "";
            }
        }

        String decryptedBlock = "";
        char nextChar;
        for(int i=0; i<binaryStrings.length; i++){
            nextChar = (char)Integer.parseInt(binaryStrings[i], 2);
            decryptedBlock += nextChar;
        }

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
}