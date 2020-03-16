package samt.smajilbasic;

import samt.smajilbasic.authentication.CurrentUser;
import samt.smajilbasic.communication.Client;

/**
 * Resources
 */
public abstract class Resources {

    /**
     * Defines the default notification length in ms.
     */
    public final static int NOTIFICATION_LENGTH = 2000;
    
    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();

    public static final String CURRENT_CLIENT_SESSION_ATTRIBUTE_KEY = Client.class.getCanonicalName();

    public static final String SIZE_MOBILE_L = "425px";
    public static final String SIZE_MOBILE_M = "375px";
    public static final String SIZE_MOBILE_S = "320px";
    public static final String SIZE_TABLET = "768px";
    public static final String SIZE_LAPTOP = "1024px";

}