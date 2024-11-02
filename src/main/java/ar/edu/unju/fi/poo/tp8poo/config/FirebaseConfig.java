package ar.edu.unju.fi.poo.tp8poo.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	private final String BUCKET_NAME = "tp8poo2024.firebasestorage.app";
	private final String workspacePath = System.getProperty("user.dir");
	private final String FIREBASE_CREDENTIALS_PATH = workspacePath
			+ "/src/main/resources/tp8poo2024-firebase-adminsdk-2htn6-5dae6d3691.json";

	@Bean
	FirebaseApp firebaseApp() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(FIREBASE_CREDENTIALS_PATH);

		@SuppressWarnings("deprecation")
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).setStorageBucket(BUCKET_NAME).build();

		return FirebaseApp.initializeApp(options);
	}
}