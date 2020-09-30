/**
 * This is a toy block cipher. It's not actually secure :)
 */
public class Main {
    public static void main(String[] args) {

    }

    /**
     * Encrypt a block of plaintext.
     *
     * @param plaintext Five characters of ASCII to encrypt.
     *
     * @param key A string consisting solely of the characters '1' and '0', 35
     * characters long. (That is, the string representation of a binary number.)
     * The secret symmetric key for this algorithm.
     */
    private static String encryptBlock(String plaintext, String key) {
        String plainTextBinary = "";

        for(int i=0; i<plainText.length; i++) {
            // Since, as a precondition, plainText only has ASCII characters,
            // this string will have length at most seven.
            String binaryDigits = Integer.toBinaryString(plainText.charAt(i));

            // Pad with zeros on the left until the string is seven digits
            // long.
            // (This part is a bit messy 🙃)
            String binaryDigitsPadded =
                String.format("%7s", binaryDigits).replaceAll(" ", "0");
            plainTextBinary += binaryDigitsPadded;
        }
        // At this point, plainTextBinary should consist of 72 binary digits.

        plainTextBinary = substring(3, plainTextBinary.length) + substring(0, 2);

        // TODO
    }

    private static String xor(String a, String b){
        String xorString = "";

        for(int i=0; i< a.length; i++){
            if(a.charAt(i) == b.charAt(i)) { xorString += "0" }
            else { xorString += "1" }
        }

        // TODO
    }
}