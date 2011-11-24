package firstapp.wirinun;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import firstapp.wirinun.db.DBHelper;

public class StoreActivity extends Activity implements TextToSpeech.OnInitListener {

    private Word word;
    private EditText englishEditText;
    private EditText hungarianEditText;
    private EditText englishSentenceEditText;
    private Button wordPronounce;
    private Button sentencePronounce;
    private TextToSpeech textToSpeech;

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        textToSpeech = new TextToSpeech(this,
                this // TextToSpeech.OnInitListener
                );

        word = (Word) getIntent().getSerializableExtra("word");

        englishEditText = (EditText) findViewById(R.id.englishEditText);
        englishSentenceEditText = (EditText) findViewById(R.id.englishSentenceEditText);
        hungarianEditText = (EditText) findViewById(R.id.hungarianEditText);

        wordPronounce = (Button) findViewById(R.id.Button01);
        wordPronounce.setOnClickListener(englishPronounceListener);

        sentencePronounce = (Button) findViewById(R.id.Button02);
        sentencePronounce.setOnClickListener(sentencePronounceListener);

        if (word.getEnglish() != null) {
            englishEditText.setText(word.getEnglish());
            wordPronounce.setEnabled(true);
        } else {
            englishEditText.setText("");
            wordPronounce.setEnabled(false);

        }
        if (word.getEnglishSentence() != null) {
            englishSentenceEditText.setText(word.getEnglishSentence());
            sentencePronounce.setEnabled(true);

        } else {
            englishSentenceEditText.setText("");
            sentencePronounce.setEnabled(false);

        }

        if (word.getHungarian() != null) {
            hungarianEditText.setText(word.getHungarian());
        } else {
            hungarianEditText.setText("");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.storemenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dbstore:

                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.insert(getWord());
                Intent myIntent = new Intent(StoreActivity.this, SimpleDictionaryActivity.class);
                StoreActivity.this.startActivity(myIntent);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private OnClickListener englishPronounceListener = new OnClickListener() {

        public void onClick(View v) {

            textToSpeech.speak(englishEditText.getText().toString(),
                    TextToSpeech.QUEUE_FLUSH, // Drop all pending entries in the playback queue.
                    null);

        }
    };
    private OnClickListener sentencePronounceListener = new OnClickListener() {

        public void onClick(View v) {
            textToSpeech.speak(englishSentenceEditText.getText().toString(),
                    TextToSpeech.QUEUE_FLUSH, // Drop all pending entries in the playback queue.
                    null);

        }
    };

    private Word getWord() {

        Word w = new Word();

        w.setEnglish(englishEditText.getText().toString());
        w.setEnglishSentence(englishSentenceEditText.getText().toString());
        w.setHungarian(hungarianEditText.getText().toString());


        return w;
    }

    public void onInit(int status) {
        // TODO Auto-generated method stub
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = textToSpeech.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
//                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.
                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                //              mAgainButton.setEnabled(true);
                // Greet the user.
                //               sayHello();
            }
        } else {
            // Initialization failed.
//            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }
}
