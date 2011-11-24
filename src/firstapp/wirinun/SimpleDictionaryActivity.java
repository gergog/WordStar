package firstapp.wirinun;

import android.app.Dialog;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import firstapp.wirinun.db.DBHelper;

public class SimpleDictionaryActivity extends Activity {

    static private int PICK_REQUEST_CODE = 0;
    private static final int STORE_DIALOG = 1;
    private IDictionaryService service;
    private boolean bound;
    private List<String> pairs = null;
    private DictionaryAdapter dictionaryAdapter;
    private Runnable viewPairs;
    private EditText textEdit;
    private long lastType;
    private ListView l1;
    private int autoSearchTimeout;
    private List<Word> selectedWords;
    //	private SparseArray<Word> wordMap;
    //	private SparseArray<Boolean> booleanMap;
    //	private Map<View, Integer> viewMap;
    private SparseArray<ViewHolder> superMap;
    private LayoutInflater mInflater;
    private LinearLayout ll;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary);
        DBHelper dbHelper = new DBHelper(getApplicationContext());



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        long tmp = prefs.getLong("CurrentLessonId", 0);

        if (tmp != 0) {
            this.setTitle("WordStar : " + dbHelper.getLesson(tmp).getName() + " lesson");
        } else {
            this.setTitle("WordStar : no lesson selected");

        }

        l1 = (ListView) findViewById(R.id.ListView01);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ll = (LinearLayout) mInflater.inflate(R.layout.store_dialog, null);

        if (ll.getParent() != null) {
            Log.v("Dictionary", "1. ll parent : " + ll.getParent().toString());
        } else {
            Log.v("Dictionary", "1. ll parent : null");

        }
//        ll = (LinearLayout) mInflater.inflate(R.layout.store_dialog, (ViewGroup) findViewById(R.id.widget33));



//        alert = new AlertDialog.Builder(this);
//        alert.setView(ll);
//        dialog = alert.create();


        try {
            autoSearchTimeout = Integer.parseInt(prefs.getString("AutoSearchTimer", "1000"));
        } catch (NumberFormatException e) {
            // "sample" was not an integer value
            // You should probably start settings again
        }

        //		booleanMap = new SparseArray<Boolean>();
        //		wordMap = new SparseArray<Word>();
        //		viewMap = new HashMap<View, Integer>();
        selectedWords = new ArrayList<Word>();
        superMap = new SparseArray<SimpleDictionaryActivity.ViewHolder>();

        textEdit = (EditText) findViewById(R.id.entry);

        textEdit.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                Date d = new Date();

                if (s.length() <= 2) {
                    lastType = d.getTime();
                } else if (s.length() > 2) {
                    if ((d.getTime() - autoSearchTimeout) > lastType) {
                        // 1 masodpercnel regebben volt leutes
                        System.out.println("autosearch invoked");
                        search();

                    } else {
                        System.out.println("no autosearch invoked");

                    }
                    lastType = d.getTime();


                }


            }
        });


        //	l1.setAdapter(new DictionaryAdapter(this));

        Button myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                search();
            }
        });


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case STORE_DIALOG:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setView(ll);
                if (ll.getParent() != null) {
                    Log.v("Dictionary", "2. ll parent : " + ll.getParent().toString());
                } else {
                    Log.v("Dictionary", "2. ll parent : null");

                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                EditText english = (EditText) ll.findViewById(R.id.englishstoredialog);
                                EditText hungarian = (EditText) ll.findViewById(R.id.hungarianstoredialog);
                                EditText example = (EditText) ll.findViewById(R.id.examplestoredialog);

                                Word w1 = new Word();
                                w1.setEnglish(english.getText().toString());
                                w1.setHungarian(hungarian.getText().toString());
                                w1.setEnglishSentence(example.getText().toString());
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                w1.setLessonId(prefs.getLong("CurrentLessonId", 0));

                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                dbHelper.insert(w1);

                                final EditText editText = (EditText) findViewById(R.id.entry);
                                editText.setText("");

                                dictionaryAdapter.clear();

                                ((FrameLayout)ll.getParent()).removeView(ll);
                                
//                                dialogLocal.dismiss();

                                // Dismiss the dialog to ensure OnDismissListeners are notified.
                                dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                // Cancel the dialog to ensure OnCancelListeners are notified.
                                dialog.cancel();
                                ((FrameLayout)ll.getParent()).removeView(ll);
                                
                                break;
                        }
                        // Remove the dialog so it is re-created next time it is required.
                        removeDialog(STORE_DIALOG);
                    }
                };
                builder.setPositiveButton("Store...", listener);
                builder.setNegativeButton(android.R.string.no, listener);
                return builder.create();
            default:
                return super.onCreateDialog(id, args);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.store:
//                Intent myIntent = new Intent(SimpleDictionaryActivity.this, StoreActivity.class);
                Word w = getSelectedToOneWord();

                Log.v("Dictionary", "Create word dialog....");


                EditText english = (EditText) ll.findViewById(R.id.englishstoredialog);
                EditText hungarian = (EditText) ll.findViewById(R.id.hungarianstoredialog);
                EditText example = (EditText) ll.findViewById(R.id.examplestoredialog);

                Log.v("Dictionary", "w : " + w.getEnglish());

                english.setText(w.getEnglish());
                hungarian.setText(w.getHungarian());
                example.setText(w.getEnglishSentence());

                showDialog(STORE_DIALOG);
                if (ll.getParent() != null) {
                    
                    
                    Log.v("Dictionary", "3. ll parent : " + ll.getParent().toString());
                } else {
                    Log.v("Dictionary", "3. ll parent : null");

                }

                /*
                alert.setPositiveButton("Store...", new DialogInterface.OnClickListener() {
                
                public void onClick(DialogInterface dialogLocal, int whichButton) {
                }
                });
                
                alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                
                public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                //                                removeDialog(dialog.);
                }
                });
                Log.v("Browse", "Dialog shown...");
                AlertDialog d = alert.show();
                //              dialog.show();
                
                Log.v("Dictionary", "Parent : " + ll.getParent().toString());
                
                
                int count = ll.getChildCount();
                
                for (int i = 0; i < count; i++) {
                View v = ll.getChildAt(i);
                
                Log.v("Dictionary", "View position: " + i);
                Log.v("Dictionary", "View : " + v.toString());
                Log.v("Dictionary", "Parent : " + v.getParent().toString());
                
                
                }
                 */


                //                myIntent.putExtra("word", w);


//                SimpleDictionaryActivity.this.startActivity(myIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bound) {
            this.bindService(
                    new Intent(SimpleDictionaryActivity.this,
                    DictionaryService.class),
                    connection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bound) {
            bound = false;
            this.unbindService(connection);
        }
    }
    private Runnable returnRes = new Runnable() {

        public void run() {
            if (pairs != null && pairs.size() > 0) {
                dictionaryAdapter.notifyDataSetChanged();
                for (int i = 0; i < pairs.size(); i++) {
                    String[] parts = StringUtils.split(pairs.get(i), "->");

                    Word w = new Word();
                    if (parts.length >= 2) {
                        String english = StringUtils.strip(parts[0]);
                        String hungarian = StringUtils.strip(parts[1]);
                        w.setEnglish(english);
                        w.setHungarian(hungarian);
                    }
                    if (parts.length >= 3) {
                        String englishSentence = StringUtils.strip(parts[2]);
                        w.setEnglishSentence(englishSentence);
                    }
                    dictionaryAdapter.add(w);
                    System.out.println("added : " + w);
                }
            } else {
                Toast.makeText(getBaseContext(), "No hit", Toast.LENGTH_LONG).show();
            }
            dictionaryAdapter.notifyDataSetChanged();
        }
    };
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                IBinder iservice) {
            service = IDictionaryService.Stub.asInterface(iservice);
            Toast.makeText(SimpleDictionaryActivity.this,
                    "Connected to Service", Toast.LENGTH_SHORT).show();
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
            Toast.makeText(SimpleDictionaryActivity.this,
                    "Disconnected from Service", Toast.LENGTH_SHORT).show();
            bound = false;
        }
    };

    protected void search() {
        final EditText editText = (EditText) findViewById(R.id.entry);
        List<Word> pairsz = new ArrayList<Word>();
        dictionaryAdapter = new DictionaryAdapter(SimpleDictionaryActivity.this, android.R.layout.simple_list_item_multiple_choice, pairsz);
        l1.setAdapter(dictionaryAdapter);
        l1.setItemsCanFocus(false);
        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        l1.setCacheColorHint(0);


        //		wordMap.clear();
        //		booleanMap.clear();
        //		viewMap.clear();
        superMap.clear();

        //		setListAdapter(pairs);

        viewPairs = new Runnable() {

            public void run() {
                getBLanguage(editText.getText().toString());
            }
        };
        Thread thread = new Thread(null, viewPairs, "DictionarySearchThread");
        thread.start();

    }

    private void getBLanguage(String aLanguage) {
        try {
            pairs = service.getLanguageBMeaning(aLanguage);
            Log.i("ARRAY", "pairs : " + pairs.size());
        } catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }
        runOnUiThread(returnRes);
    }

    private Word getSelectedToOneWord() {

        Word w = new Word();
        StringBuffer hungarian = new StringBuffer("");
        StringBuffer english = new StringBuffer("");
        StringBuffer englishSentence = new StringBuffer("");


        for (Iterator iterator = selectedWords.iterator(); iterator.hasNext();) {
            Word word = (Word) iterator.next();



            if (english.length() != 0) {
                if (english.toString().contains(StringUtils.strip(word.getEnglish()))) {
                    hungarian.append("; " + StringUtils.strip(word.getHungarian()));

                    if (englishSentence.length() == 0) {
                        englishSentence.append(StringUtils.strip(word.getEnglishSentence()));

                    }
                    Log.v("Dictionary", "english : " + english + " hungarian : " + hungarian);

                } else {
                    Log.v("Dictionary", "other english : " + english + " hungarian : " + hungarian);

                }
            } else {
                english.append(StringUtils.strip(word.getEnglish()));
                hungarian.append(StringUtils.strip(word.getHungarian()));

                Log.v("Dictionary", "english : " + english + " hungarian : " + hungarian);


                englishSentence.append(StringUtils.strip(word.getEnglishSentence()));

            }

            //                wf.setFormObject(formObject);

//            System.out.println("english : " + english);
//            System.out.println("hungarian : " + hungarian);
//            System.out.println("englishSentence : " + englishSentence);


        }

        w.setEnglish(english.toString());
        w.setHungarian(hungarian.toString());
        w.setEnglishSentence(englishSentence.toString());

        return w;


    }

    private void registerSelectedWords() {


        int count = l1.getChildCount();

        selectedWords.clear();

        for (int i = 0; i < count; i++) {
            LinearLayout linearLayout = (LinearLayout) l1.getChildAt(i);
            CheckedTextView checkedTextView1 = (CheckedTextView) linearLayout.getChildAt(0);
            CheckedTextView checkedTextView2 = (CheckedTextView) linearLayout.getChildAt(1);

            if (checkedTextView1.isChecked()) {
                selectedWords.add((((ViewHolder) linearLayout.getTag()).getWord()));

//                System.out.println("selectedWords : " + selectedWords.get(selectedWords.size() - 1));
            }
            if (checkedTextView2.isChecked()) {
                //				System.out.println("Checked2 : " + checkedTextView1.getText() + " : " + checkedTextView2.getText());
            }

        }


    }

    private class DictionaryAdapter extends ArrayAdapter<Word> {

        private List<Word> items;

        public DictionaryAdapter(Context context, int textViewResourceId, List<Word> items) {
            super(context, textViewResourceId, items);
            this.items = items;

        }
        public OnClickListener myClickListener = new OnClickListener() {

            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) v;


                // TODO if it is Lesson, then it should go inside the lesson

                CheckedTextView textView = (CheckedTextView) linearLayout.getChildAt(0);

                CheckedTextView textView1 = (CheckedTextView) linearLayout.getChildAt(1);

                ViewHolder vh = (ViewHolder) v.getTag();



                textView.toggle();


//                System.out.println("checked (" + textView.getText() + "/" + textView1.getText() + ") at " + vh.getPosition());

                vh.setSelected(textView.isChecked());

                registerSelectedWords();


            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Word o = items.get(position);

            ViewHolder vh = superMap.get(position);

            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vh == null) {
                Log.v("Dictionary", "Inflating view");
                v = vi.inflate(R.layout.row, null);
                v.setClickable(true);
                v.setOnClickListener(myClickListener);

                vh = new ViewHolder();
                vh.setPosition(position);
                vh.setSelected(false);
                vh.setWord(o);
                superMap.append(position, vh);
                v.setTag(vh);

                //				System.out.println("size of map : " + superMap.size());
            } else if (v == null) {
                Log.v("Dictionary", "View null");
                Log.v("Dictionary", "View v : " + v);
                Log.v("Dictionary", "position : " + position);
                Log.v("Dictionary", "word : " + o.getEnglish());
                Log.v("Dictionary", "word : " + vh.getWord().getEnglish());
                v = vi.inflate(R.layout.row, null);
                v.setClickable(true);
                v.setOnClickListener(myClickListener);
                v.setTag(vh);

            } else {
                Log.v("Dictionary", "No inflating view");
                Log.v("Dictionary", "View v : " + v);
                Log.v("Dictionary", "position : " + position);
                Log.v("Dictionary", "word : " + o.getEnglish());
            }


            if (o != null) {
                String english = o.getEnglish();
                String hungarian = o.getHungarian();
                CheckedTextView tt = (CheckedTextView) v.findViewById(R.id.toptext);
                tt.setChecked(vh.isSelected());

                CheckedTextView bt = (CheckedTextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText("" + english);
                }
                if (bt != null) {
                    bt.setText("	" + hungarian);
                }
                v.setTag(vh);
                //				}

            }
            return v;
        }
    }

    private class ViewHolder {

        private boolean selected;
        private Word word;
        private int position;

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setWord(Word word) {
            this.word = word;
        }

        public Word getWord() {
            return word;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }
}
