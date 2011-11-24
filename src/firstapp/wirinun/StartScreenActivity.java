package firstapp.wirinun;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.NIOFSDirectory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class StartScreenActivity extends Activity {

    static private int PICK_SAVEREQUEST_CODE = 0;
    static private int PICK_LOADREQUEST_CODE = 1;
    public static final String PREFS_NAME = "MyPreferencesFile";
    private String directory;
    private String defaultDictionary;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        System.out.println("onCreateOPtionsMenu start");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

//        System.out.println("onCreateOPtionsMenu end");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        openOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createdictionarymenu:
                createDictionary();
                return true;
            case R.id.loadfilemenu:
                loadDictionary();
                return true;
            case R.id.loadlastmenu:
                if (defaultDictionary.length() > 0) {
                    loadDictionary(defaultDictionary);
                } else {
                    Toast.makeText(getApplicationContext(), "No default file set.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.browsedicmenu:
                Intent browseIntent = new Intent(StartScreenActivity.this, BrowseActivity.class);
                StartScreenActivity.this.startActivity(browseIntent);
                return true;
            case R.id.search:
                Intent myIntent = new Intent(StartScreenActivity.this, SimpleDictionaryActivity.class);
                StartScreenActivity.this.startActivity(myIntent);
                return true;
//            case R.id.web:
//                Uri uri = Uri.parse("http://www.cnn.com");
//			 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                Intent intent = new Intent(StartScreenActivity.this, WebActivity.class);
//                startActivity(intent);
//                return true;
            case R.id.settingsmenu:
                Intent settingsActivity = new Intent(getBaseContext(),
                        MyPreferencesActivity.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDictionary() {
        Intent intent1 = new Intent();

        intent1.setAction(Intent.ACTION_PICK);
        Uri startDir = Uri.fromFile(new File(directory));
        // Files and directories
        intent1.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.directory");
        // Optional filtering on file extension.
        //				intent1.putExtra("browser_filter_extension_whitelist", "*.idx");
        // Title
        intent1.putExtra("explorer_title", "Select a directory");
        // Optional colors
        intent1.putExtra("browser_title_background_color", "440000AA");
        intent1.putExtra("browser_title_foreground_color", "FFFFFFFF");
        intent1.putExtra("browser_list_background_color", "66000000");
        // Optional font scale
        intent1.putExtra("browser_list_fontscale", "120%");
        // Optional 0=simple list, 1 = list with filename and size, 2 = list with filename, size and date.
        intent1.putExtra("browser_list_layout", "2");
        startActivityForResult(intent1, PICK_LOADREQUEST_CODE);

    }

    protected void createDictionary() {
        Intent intent1 = new Intent();

        intent1.setAction(Intent.ACTION_PICK);
        Uri startDir = Uri.fromFile(new File(directory));
        // Files and directories
        intent1.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
        // Optional filtering on file extension.
        intent1.putExtra("browser_filter_extension_whitelist", "*.txt");
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
        startActivityForResult(intent1, PICK_SAVEREQUEST_CODE);

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        openOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOADREQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String type = data.getType();
                if (uri != null) {
                    String path = uri.toString();
                    if (path.toLowerCase().startsWith("file://")) {
                        // Selected file/directory path is below
                        File f = new File(URI.create(path));
                        path = f.getAbsolutePath();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("defaultDict", path);
                        
                        Log.v("StartScreen", "defaultDict stored : " + path);
                        
                        loadDictionary(path);
                        editor.commit();

                    }
                }
            }
        }

        if (requestCode == PICK_SAVEREQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String type = data.getType();
                if (uri != null) {
                    String path = uri.toString();
                    if (path.toLowerCase().startsWith("file://")) {
                        // Selected file/directory path is below
                        File f = new File(URI.create(path));
                        path = f.getAbsolutePath();
                        Intent intent1 = null;

                        if (requestCode == PICK_SAVEREQUEST_CODE) {
                            intent1 = new Intent(StartScreenActivity.this, CreateDictionaryActivity.class);
                        } else if (requestCode == PICK_LOADREQUEST_CODE) {



                            intent1 = new Intent(StartScreenActivity.this, LoadDictionaryActivity.class);
                        }

                        intent1.setAction(Intent.ACTION_RUN);
                        intent1.putExtra("dictionary_name", path);

                        startActivity(intent1);



                    }

                }
            }
        }

    }

    protected void loadDictionary(String filename) {


        try {
            NIOFSDirectory fsDir = new NIOFSDirectory(new File(filename));

            if (IndexReader.indexExists(fsDir)) {
                fsDir.close();

                Toast.makeText(StartScreenActivity.this, "Dictionary loaded", Toast.LENGTH_LONG).show();


                Intent intent1 = new Intent(StartScreenActivity.this, DictionaryService.class);
                intent1.putExtra("index_place", filename);

                startService(intent1);

                //				finish();
                //		openOptionsMenu();


            } else {
                Toast.makeText(StartScreenActivity.this, "No index exist in this directory", Toast.LENGTH_LONG).show();

            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if (prefs.getBoolean("autoloadlast", false)) {
            if (defaultDictionary.length() > 0) {
                loadDictionary(defaultDictionary);
            } else {
                Toast.makeText(getApplicationContext(), "No default file set.", Toast.LENGTH_LONG).show();
            }





        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        directory = prefs.getString("dictfolder",
                "/sdcard/dict/");
        Log.v("StartScreen", "Dictionary folder : " + directory);
        defaultDictionary = prefs.getString("defaultDict", "");
        Log.v("StartScreen", "Default dictionary : " + defaultDictionary);

        if (prefs.getBoolean("autoloadlast", false)) {
            if (defaultDictionary.length() > 0) {
                loadDictionary(defaultDictionary);
            } else {
                Toast.makeText(getApplicationContext(), "No default file set.", Toast.LENGTH_LONG).show();
            }





        }



    }
}
