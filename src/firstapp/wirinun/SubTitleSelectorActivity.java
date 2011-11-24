package firstapp.wirinun;

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubTitleSelectorActivity extends Activity {

	static private int PICK_REQUEST_CODE = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subtitle_selection);
		
		Button myButton = (Button) findViewById(R.id.button);		
		myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
        	    Intent intent1 = new Intent();
        	    
        	    intent1.setAction(Intent.ACTION_PICK);
        	    Uri startDir = Uri.fromFile(new File("/sdcard"));
        	    // Files and directories
        	    intent1.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
        	    // Optional filtering on file extension.
        	    intent1.putExtra("browser_filter_extension_whitelist", "*.srt");
        	    // Title
        	    intent1.putExtra("explorer_title", "Select a file");
        	    // Optional colors
        	    intent1.putExtra("browser_title_background_color", "440000AA");
        	    intent1.putExtra("browser_title_foreground_color", "FFFFFFFF");
        	    intent1.putExtra("browser_list_background_color", "66000000");
        	    // Optional font scale
        	    intent1.putExtra("browser_list_fontscale", "120%");
        	    // Optional 0=simple list, 1 = list with filename and size, 2 = list with filename, size and date.
        	    intent1.putExtra("browser_list_layout", "2");
        	    startActivityForResult(intent1, PICK_REQUEST_CODE);	    
            	
        	    
        	    
            	
            }
        });
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		

		if (requestCode == PICK_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
			      String type = data.getType();
			      if (uri != null)
			      {
			         String path = uri.toString();
			         if (path.toLowerCase().startsWith("file://"))
			         {
			            // Selected file/directory path is below
			        	File f = new File(URI.create(path)); 
			            path = f.getAbsolutePath();
			            
			    		TextView tv = (TextView) findViewById(R.id.text);		
			            
			    		tv.setText(path);
			            
			    		
			    		
			    		
			    		
			         }

			      }				
			}
		}
		
	}

}
