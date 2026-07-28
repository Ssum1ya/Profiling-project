package agent;

public class MethodNameUtil {
    public static String TARGET_PACKAGE;

    public static String simplify(String fullSignature) {
        int firstIndex = fullSignature.indexOf(TARGET_PACKAGE);
        if (firstIndex == -1) {
            return fullSignature;
        }

        int secondIndex = fullSignature.indexOf(TARGET_PACKAGE, firstIndex + TARGET_PACKAGE.length());

        if (secondIndex == -1) {
            return fullSignature.substring(firstIndex);
        }

        return fullSignature.substring(secondIndex);
    }
}
