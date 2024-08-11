package sia.tcloud3.service.email;

public class EmailBuilder {

    public static String buildEmail(String name, String link) {
        return "Hi " + name + ", your link " + link;
    }
}
