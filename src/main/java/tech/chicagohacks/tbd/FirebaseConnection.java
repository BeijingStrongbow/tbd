package tech.chicagohacks.tbd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseConnection {
	
	private DatabaseReference readLinkRef;
		
	private DatabaseReference pushVideoDataRef;
		
	private String email;
	
	public FirebaseConnection(String databaseUrl){
	
		try{
			FileInputStream key = new FileInputStream("project-61475-firebase-adminsdk-b5jjb-6e711a3f18.json");
			
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setDatabaseUrl(databaseUrl)
					.setCredential(FirebaseCredentials.fromCertificate(key))
					.build();
			
			FirebaseApp.initializeApp(options);
			
		}
		catch(FileNotFoundException ex){
			System.out.println("Couldn't load Firebase key");
			return;
		}
		catch(IOException ex){
			System.out.println("IOException with firebase.");
			return;
		}
	
		readLinkRef = FirebaseDatabase.getInstance().getReference("Link");

		pushVideoDataRef = FirebaseDatabase.getInstance().getReference("Stories");
				
		readLinkRef.addValueEventListener(new ValueEventListener(){

			public void onCancelled(DatabaseError arg0) {}

			public void onDataChange(DataSnapshot snapshot) {
				String url = (String) snapshot.child("link").getValue();
				email = (String) snapshot.child("email").getValue();
				System.out.println(url);
				
				App.setVideoUrl(url);
				//App.setVideoUrl("https://www.youtube.com/watch?v=Vd3K03LaW5U");
			}
		});
	}
	
	public void pushVideoData(String title, String story){
		String newStoryKey = pushVideoDataRef.push().getKey();
		DatabaseReference newStory = pushVideoDataRef.child(newStoryKey);
		newStory.child("title").setValue(title);
		newStory.child("story").setValue(story);
		newStory.child("email").setValue(email);
	}
	
	public void writeUrlError(){
		readLinkRef.setValue("Invalid URL");
	}
	
	public void addProgress(final int increment){
		readLinkRef.addListenerForSingleValueEvent(new ValueEventListener(){

			@Override
			public void onCancelled(DatabaseError arg0) {}

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				int value = (Integer) snapshot.child("progress").getValue();
				value += increment;
				readLinkRef.child("progress").setValue(value);
			}	
		});
	}
}
