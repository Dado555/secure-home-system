package rs.securehome.admin.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class PasswordGenerator {

    public static char[] generatePassword(int length) throws NoSuchAlgorithmException, NoSuchProviderException {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "1234567890";
        String specialCharacters = "!@#$";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< length ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }

        fisherYatesShuffle(password, random);
        return password;
    }

    private static void fisherYatesShuffle(char[] array, SecureRandom random)
    {
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            char a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public static boolean validatePasswordStrength(String password) {
        return password.length() >= 8 &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[!@#$].*");
    }
}
