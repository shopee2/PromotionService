package shopee2.pormotionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Service
@ComponentScan(basePackages="shopee2.controller")
public class FirebaseInit {
	
	@PostConstruct
	public void initialize() throws IOException {
		System.out.println(new File(".").getAbsolutePath());
		
		FileInputStream serviceAccount = new FileInputStream("./src/main/resources/serviceAccountKey.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://sop-promotion.firebaseio.com").build();

		//FirebaseApp.initializeApp(options);
		
		if(FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
		
	}
	public Firestore getFireBase(){
		return FirestoreClient.getFirestore();
	}
}
