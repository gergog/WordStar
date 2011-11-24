/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstapp.wirinun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import firstapp.wirinun.db.DBHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ETHGGY
 */
public class BrowseActivity extends Activity {

    private SparseArray<ViewHolder> superMap;
    private ListView l1;
    private DictionaryAdapter dictionaryAdapter;
    private Runnable viewPairs;
    private Menu menu;
    private long currentLessonId = 0;
    private DBHelper dbHelper;
    private final static int DELETE_LESSON = 1;
    private final static int DELETE_WORDS = 2;
    private final static int DELETE_WORD = 3;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here        

        dbHelper = new DBHelper(getApplicationContext());


        setContentView(R.layout.browse);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentLessonId = prefs.getLong("CurrentLessonId", 0);
        Log.v("Browse", "Current lesson : " + currentLessonId);

        l1 = (ListView) findViewById(R.id.ListView01);
        superMap = new SparseArray<BrowseActivity.ViewHolder>();
        updateList(currentLessonId);




    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Log.v("Browse", "onCreateContextMenu");
        ListView listView = (ListView) v;

        AdapterView.AdapterContextMenuInfo aaa = (AdapterView.AdapterContextMenuInfo) menuInfo;

        LinearLayout linearLayout = (LinearLayout) aaa.targetView;

        ViewHolder viewHolder = superMap.get(aaa.position);

        View tmp = linearLayout.findViewById(R.id.englishtext);

        Log.v("Browse", "type : " + tmp.getClass());
        
        
        TextView textView = (TextView) linearLayout.findViewById(R.id.englishtext);

        if (viewHolder == null) {
            Log.v("Browse", "Viewholder is null");
            Log.v("Browse", "textView : " + textView.getText());
        } else {
            if (viewHolder.getObject() instanceof Word) {
                menu.setHeaderTitle("Word : " + ((Word) viewHolder.getObject()).getEnglish());
                menu.add(Menu.NONE, DELETE_WORD, Menu.NONE, "Delete");
            } else if (viewHolder.getObject() instanceof Lesson) {
                menu.setHeaderTitle("Lesson : " + ((Lesson) viewHolder.getObject()).getName());
                menu.add(Menu.NONE, DELETE_WORDS, Menu.NONE, "Delete only words");
                menu.add(Menu.NONE, DELETE_LESSON, Menu.NONE, "Delete lesson & words");
            }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        dbHelper = new DBHelper(getApplicationContext());

        ViewHolder vh = superMap.get(info.position);


        switch (item.getItemId()) {
            case BrowseActivity.DELETE_LESSON:
                Lesson l = (Lesson) vh.getObject();
                Log.v("Browse", "Delete " + l.getName() + " selected");
                long parentLessonId = l.getParentId();
                dbHelper.deleteLessonWithWords(l.getId());
                updateList(parentLessonId);
                return true;
            case BrowseActivity.DELETE_WORDS:
                Lesson l1 = (Lesson) vh.getObject();
                Log.v("Browse", "Delete " + l1.getName() + " selected");
                dbHelper.deleteLessonWords(l1.getId());
                updateList(currentLessonId);
                return true;
            case BrowseActivity.DELETE_WORD:
                Word w1 = (Word) vh.getObject();
                Log.v("Browse", "Delete " + w1.getEnglish() + " selected");
                dbHelper.deleteWord(w1.getId());
                updateList(currentLessonId);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbHelper.cleanup();

    }

    @Override
    protected void onResume() {
        super.onResume();


        dbHelper.establishDb();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browsemenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.createlesson:
                Log.v("Browse", "Create lesson selected...");
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();

                        Lesson l = new Lesson();
                        l.setName(value);
                        l.setParentId(currentLessonId);
                        dbHelper.insert(l);

                        updateList(currentLessonId);


                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                Log.v("Browse", "Dialog shown...");
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void updateList(long id) {
        List<Object> pairsz = new ArrayList<Object>();

        if (dictionaryAdapter == null) {
            dictionaryAdapter = new DictionaryAdapter(BrowseActivity.this, android.R.layout.simple_list_item_multiple_choice, pairsz);
            l1.setAdapter(dictionaryAdapter);
            l1.setItemsCanFocus(false);
            l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            l1.setCacheColorHint(0);
        }
        dictionaryAdapter.clear();
        // TODO Ide beadhatnank a eppen aktualis lecke nevet




        //		wordMap.clear();
        //		booleanMap.clear();
        //		viewMap.clear();
        superMap.clear();

        dictionaryAdapter.notifyDataSetChanged();


        if (id != 0) {
            Lesson current = dbHelper.getLesson(id);
            dictionaryAdapter.add(current);

        }
//        DBHelper_Lesson dbHelperLesson = new DBHelper_Lesson(getApplicationContext());
        ArrayList<Lesson> lessons = dbHelper.getAllLessoninLesson(id);

        for (Iterator<Lesson> it = lessons.iterator(); it.hasNext();) {
            Lesson lesson = it.next();

            dictionaryAdapter.add(lesson);


        }

        ArrayList<Word> words = dbHelper.getAllWordinLesson(id);

        for (Iterator<Word> it = words.iterator(); it.hasNext();) {
            Word word = it.next();

            dictionaryAdapter.add(word);


        }

        // to be sure
        currentLessonId = id;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor editor = prefs.edit();

        Log.v("Browse", "adding currentLessonId : " + currentLessonId);
        editor.putLong("CurrentLessonId", currentLessonId);
        editor.commit();

        dictionaryAdapter.notifyDataSetChanged();

        registerForContextMenu(l1);

        Log.v("Browse", "Current lesson : " + prefs.getLong("CurrentLessonId", 0));

        //		setListAdapter(pairs);


    }

    private class DictionaryAdapter extends ArrayAdapter<Object> {

        private List<Object> items;

        public DictionaryAdapter(Context context, int textViewResourceId, List<Object> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        public OnClickListener iconClickListener = new OnClickListener() {

            public void onClick(View v) {
                Log.v("Browse", "onClick");
                LinearLayout linearLayout = (LinearLayout) v;

                ViewHolder vh = (ViewHolder) v.getTag();


                if (vh.getObject() instanceof Word) {
                    Log.v("Browse", "Icon onClick on Word");
//                    textView.toggle();
//                    vh.setSelected(textView.isChecked());
                    // It could be needed later
                } else if (vh.getObject() instanceof Lesson) {
                    Log.v("Browse", "Icon onClick on Lesson");


                }
            }
        };
        public OnClickListener myClickListener = new OnClickListener() {

            public void onClick(View v) {
                Log.v("Browse", "onClick");
                LinearLayout linearLayout = (LinearLayout) v;

                ViewHolder vh = (ViewHolder) v.getTag();


                if (vh != null) {
                    if (vh.getObject() instanceof Word) {
//                    textView.toggle();
//                    vh.setSelected(textView.isChecked());
                        // It could be needed later
                    } else if (vh.getObject() instanceof Lesson) {
                        Log.v("Browse", "onClick on Lesson");
                        Lesson l = (Lesson) vh.getObject();

                        if (l.getId() == currentLessonId) {
                            currentLessonId = l.getParentId();
                        } else {
                            currentLessonId = l.getId();

                        }
                        updateList(currentLessonId);


                    }
                }

            }
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup v = (ViewGroup) convertView;

            Object o = items.get(position);

            ViewHolder vh = superMap.get(position);

            if (vh == null) {

                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = (ViewGroup) vi.inflate(R.layout.browse_row, null);
                TextView tt = (TextView) v.findViewById(R.id.englishtext);
                LinearLayout ll1 = (LinearLayout) v.findViewById(R.id.br_ll1);
                ImageView iv1 = (ImageView) v.findViewById(R.id.br_image1);
                Log.v("Browse", "vh in pos " + position + " is null for " + tt.getText());
                ll1.setClickable(true);
                ll1.setLongClickable(true);
                ll1.setOnClickListener(myClickListener);

                iv1.setClickable(true);
                iv1.setOnClickListener(iconClickListener);




                vh = new ViewHolder();
                vh.setPosition(position);
                vh.setSelected(false);
                vh.setObject(o);
                superMap.append(position, vh);
                
                // we need to set everywhere
                v.setTag(vh);
                ll1.setTag(vh);
                iv1.setTag(vh);

            }


            if (o != null) {

                if (o instanceof Word) {
                    // TODO a csillagot kicserelhetnenk egy lefele mutato nyillal, ami inflate-elne a view-t
                    Word w = (Word) o;
                    String english = w.getEnglish();
                    String hungarian = w.getHungarian();
                    String englishSentence = w.getEnglishSentence();
                    Log.v("Browse", "getView : " + english);

                    ImageView iv1 = (ImageView) v.findViewById(R.id.br_image1);
                    TextView tt = (TextView) v.findViewById(R.id.englishtext);

//                    tt.setChecked(vh.isSelected());

                    TextView bt = (TextView) v.findViewById(R.id.hungariantext);
                    if (tt != null) {
                        tt.setText("" + english);
                    }
                    if (bt != null) {
                        bt.setText("	" + hungarian);
                    }
                    TextView est = (TextView) v.findViewById(R.id.englishSentence);
                    if (est != null) {
                        est.setText("	" + englishSentence);
                    }

                    if (vh.isSelected()) {
                        Drawable d = getResources().getDrawable(R.drawable.expander_ic_maximized);
                        iv1.setBackgroundDrawable(d);
                        
                        vh.setSelected(false);
                    } else {
                        Drawable d = getResources().getDrawable(R.drawable.expander_ic_minimized);
                        iv1.setBackgroundDrawable(d);
                        vh.setSelected(true);

                    }



                } else if (o instanceof Lesson) {
                    Lesson l = (Lesson) o;
                    String name = l.getName();
                    Log.v("Browse", "getView : " + name);
                    TextView tt = (TextView) v.findViewById(R.id.englishtext);



                    //				System.out.println("position (" + english + "/" + hungarian + ") " + position) ;
//                    tt.setChecked(vh.isSelected());
                    if (tt != null) {
                        tt.setText("Lesson");
                    }

                    TextView bt = (TextView) v.findViewById(R.id.hungariantext);
                    if (bt != null) {
                        bt.setText("	" + name);
                    }

                }

                Log.v("Browse", "Setting viewholder : " + vh);
                v.setTag(vh);
                //				}

            } else {
                Log.v("Browse", "object is null");
            }
            return v;
        }
    }

    private class ViewHolder {

        private boolean selected;
        private Object object;
        private int position;

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }
}
