package firstapp.wirinun;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.NIOFSDirectory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class LoadDictionaryActivity extends Activity {

    private String dictionaryFilename = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.selectfile);
	
	dictionaryFilename = getIntent().getStringExtra("dictionary_name");
	
	
//	loadButton = (Button) findViewById(R.id.load);
//	loadButton.setText("Load");
		


    }

   


} 
