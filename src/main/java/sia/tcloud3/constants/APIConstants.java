package sia.tcloud3.constants;
import java.net.URLEncoder;

public class APIConstants {
    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;

    public static final String PAYSTACK_INIT = "https://api.paystack.co/plan";
    public static final String PAYSTACK_INITIALIZE_PAY = "https://api.paystack.co/transaction/initialize";
    public static final String PAYSTACK_VERIFY = "https://api.paystack.co/transaction/verify/";
    public static final String GOOGLE_OAUTH2_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_OAUTH2_URL_2 = "https://www.googleapis.com/oauth2/v4/token";
    public static final String GOOGLE_OAUTH2_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    public static final String GOOGLE_USER_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static final String GOOGLE_USER_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
}
