package helperClass;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by aruna.ramakrishnan on 8/25/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    SessionManager sessionManager;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setRegistrationId(refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/allUsers");
        // TODO: Implement this method to send any registration to your app's servers.

    }
}
