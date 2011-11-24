package firstapp.wirinun;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

public class WebActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myweb);
		
		WebView webview = (WebView) findViewById(R.id.WebView01);		
		
		webview.loadUrl("http://slashdot.org/");
	
	}

}
