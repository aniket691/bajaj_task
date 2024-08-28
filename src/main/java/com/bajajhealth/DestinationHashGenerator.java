import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 2) {
            System.out.println("Please provide the PRN number and JSON file path.");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

     
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

       
        String destinationValue = findDestination(rootNode);
        if (destinationValue == null) {
            System.out.println("No 'destination' key found in the JSON file.");
            return;
        }

       
        String randomString = generateRandomString(8);

      
        String toHash = prnNumber + destinationValue + randomString;

      
        String hash = generateMD5Hash(toHash);

    
        System.out.println(hash + ";" + randomString);
    }

    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestination(field.getValue());
                if (result != null) return result;
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                String result = findDestination(arrayItem);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
