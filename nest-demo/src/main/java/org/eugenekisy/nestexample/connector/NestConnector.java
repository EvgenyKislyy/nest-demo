package org.eugenekisy.nestexample.connector;

import org.eugenekisy.nestexample.util.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

@Component
public class NestConnector {
	
	private static  Logger logger = LoggerFactory.getLogger(NestConnector.class);

	private static final String FIREBASE_URL = "firebase.url";
	private static final String APP_TOKEN = "app.token";
	

	@Autowired
	private Environment env;
	
	private String firebaseURL;
	private String appToken;

	private Firebase fireBase;
	private boolean authenticated = false;

	public interface AuthenticationListener {
		void onAuthenticationSuccess(String string);

		void onAuthenticationFailure(int errorCode);
	}

	public interface EventListener {
		void onDataChange(String value);
		void onCancelled(int errorCode);
	}
	
	

	public NestConnector() {

	}


	public void authenticate( AuthenticationListener listener) {
		firebaseURL = env.getProperty(FIREBASE_URL);
		appToken = env.getProperty(APP_TOKEN);
		fireBase = new Firebase(firebaseURL);
		fireBase.authWithCustomToken(appToken, new NestFirebaseAuthListener(listener));
	}


	public void printAll(EventListener eventListener) {
		fireBase.addListenerForSingleValueEvent(new NestVaueEventListener(eventListener));
	}

	public void printAllDevices(EventListener eventListener) {
		fireBase.child(Keys.DEVICES).addListenerForSingleValueEvent(new NestVaueEventListener(eventListener));
	}

	public void setDeviceParam(String deviceType, String deviceId, String parameter, Object value) {
		fireBase.child(Keys.DEVICES).child(deviceType).child(deviceId).child(parameter).setValue(value);
	}

	public void addDeviceListener(String deviceType, String deviceId, String parameter, EventListener eventListener) {
		fireBase.child(Keys.DEVICES).child(deviceType).child(deviceId).child(parameter)
				.addValueEventListener(new NestVaueEventListener(eventListener));
	}
	
	public void removeEventListener(String deviceType, String deviceId, String parameter, EventListener eventListener) {
		fireBase.child(Keys.DEVICES).child(deviceType).child(deviceId).child(parameter)
				.removeEventListener(new NestVaueEventListener(eventListener));
	}


	private class NestFirebaseAuthListener implements Firebase.AuthResultHandler {
		private AuthenticationListener mListener;

		public NestFirebaseAuthListener(AuthenticationListener listener) {
			mListener = listener;
		}

		@Override
		public void onAuthenticated(AuthData arg0) {
			mListener.onAuthenticationSuccess(arg0.toString());

		}

		@Override
		public void onAuthenticationError(FirebaseError arg0) {
			mListener.onAuthenticationFailure(arg0.getCode());
		}

	}

	private class NestVaueEventListener implements ValueEventListener {
		private EventListener mListener;

		public NestVaueEventListener(EventListener listener) {
			mListener = listener;
		}

		@Override
		public void onCancelled(FirebaseError arg0) {
			mListener.onCancelled(arg0.getCode());

		}

		@Override
		public void onDataChange(DataSnapshot arg0) {
			mListener.onDataChange(arg0.getValue().toString());

		}
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}

}
