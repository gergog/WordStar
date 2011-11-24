package firstapp.wirinun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateDictionaryActivity extends Activity {
	static private int PICK_REQUEST_CODE = 0;
	static private int LOAD_REQUEST_CODE = 1;
	static private final int DIALOG_PROGRESS = 1;

	private String dictionaryFilename = "";
	private ProgressBar mProgress;
	private int mProgressStatus = 0;
	private ProgressDialog progDialog = null;

	private Analyzer analyzer = new org.apache.lucene.analysis.snowball.SnowballAnalyzer(org.apache.lucene.util.Version.LUCENE_30, "English");
	private IndexWriter w;
	//    private IndexReader r;
	private Directory ramDirectory;      

	private Button loadButton;
	private EditText editText;
	private Spinner spinner;
	private TextView indexStatus;
	private CheckBox mergeCheckBox;

	private int maxLineNumber = 10000;
	private String separator;
	private boolean merge = false;


	private void determineFileStatus(String s) {
		File f = new File(s);

		System.out.println("determine filestatus : " + s);

		if (f.exists()) {
			indexStatus.setTextColor(Color.RED);
			indexStatus.setTextSize(20f);
			indexStatus.setTypeface(Typeface.DEFAULT_BOLD);
			indexStatus.setPadding(5, 5, 5, 5);
			mergeCheckBox.setEnabled(true);
			indexStatus.setText("file exist");



		} else {
			indexStatus.setTextColor(Color.GREEN);
			indexStatus.setTextSize(20f);
			indexStatus.setTypeface(Typeface.DEFAULT_BOLD);
			indexStatus.setPadding(5, 5, 5, 5);
			mergeCheckBox.setEnabled(false);
			indexStatus.setText("Ok");

		}

	}

	private String getIndexDirectory(String dFile) {
		File f1 = new File(dFile);

		StringBuilder sb = new StringBuilder((f1.getParent() == null ? "" : f1.getParent()));
		sb.append("/dict/");
		sb.append(StringUtils.removeEnd(f1.getName(), ".txt"));
		sb.append(".idx");

		return sb.toString();

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectfile);
		loadButton = (Button) findViewById(R.id.load);		

		indexStatus = (TextView) findViewById(R.id.filestatus);

		mergeCheckBox = (CheckBox) findViewById(R.id.mergecheck);
		mergeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				merge = isChecked;
			}
		});

		editText = (EditText) findViewById(R.id.filenameedit);
		editText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(Editable s) {
				determineFileStatus(s.toString());
			}
		});


		dictionaryFilename = getIntent().getStringExtra("dictionary_name");
		editText.setText(getIndexDirectory(dictionaryFilename));
		String d1 = editText.getText().toString();


		spinner = (Spinner) findViewById(R.id.Spinner01);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			this, R.array.separator_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);	
		spinner.setOnItemSelectedListener(new MySeparatorSpinnerListener());

		separator = (String)spinner.getSelectedItem();


		//		Directory index = new RAMDirectory();
		//		FSDirectory dir = FSDirectory.open(new File(indexDir));		

		loadButton = (Button) findViewById(R.id.load);		
		loadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Directory index;
				try {

					index = new NIOFSDirectory(new File(getIndexDirectory(dictionaryFilename)));
					ramDirectory = index;
					try {
						//			w = new IndexWriter(index, analyzer, true, new MaxFieldLength(400000));
						w = new IndexWriter(index, analyzer, !merge, IndexWriter.MaxFieldLength.UNLIMITED);

						//			r = IndexReader.open(ramDirectory, true);


					} catch (CorruptIndexException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (LockObtainFailedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					w.setSimilarity(new MySimilarity()); 
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				Thread runner = null;
				showDialog(DIALOG_PROGRESS);
				//				progDialog.setMax(1);
				//				System.out.println("maxlinenumber : " + maxLineNumber);
				final Handler myHandler = new Handler() {
					public void handleMessage (Message m) {

						//						progDialog.setMax(progDialog.getProgress() + 1);
						//						progDialog.incrementProgressBy(1);
						//						System.out.println("max : " + progDialog.getMax() + " current : " + progDialog.getProgress());

						if (m.getData().getInt("end") == 1) {
							System.out.println("read is over");

							progDialog.dismiss(); 

							finish();

						}
						/*						
						if (progDialog.getMax() == progDialog.getProgress()) {
						}
						 */					
						//updateUIHere();
					}
				};




				runner = new Thread() {
					public void run() {
						// just doing some long operation
						Reader reader = null;
						try {

							reader = new InputStreamReader(new FileInputStream(dictionaryFilename), "ISO-8859-2");
							BufferedReader lineReader = new BufferedReader(reader,32768);
							String line;
							int i = 0;
							while ((line = lineReader.readLine()) != null) {

								String[] parts = StringUtils.split(line, separator);

								if (parts.length >= 2) {

									String english = StringUtils.strip(parts[0]);
									String hungarian = StringUtils.strip(parts[1]);

									if (parts.length >= 3) {
										String englishSentence = StringUtils.strip(parts[2]);
									}

									if (parts.length >= 4) {
										String hungarianSentence = StringUtils.strip(parts[3]);
									}


									addDoc(w, parts);


								} else {
									Toast.makeText(CreateDictionaryActivity.this, "Wrong format", Toast.LENGTH_LONG).show();
								}


								if (i%1000 == 0) {
									w.commit();

									System.out.println("Read " + i);

									//				    System.out.println("write " + r.numDocs());

									Message m = myHandler.obtainMessage();
									Bundle b = new Bundle();
									b.putInt("counter", i/1000);
									m.setData(b);
									myHandler.sendMessage(m);

								}
								/*
									if (i>maxLineNumber) {
										System.out.println("break");
										break;
									}
								 */
								//System.out.println("i : " + i + " " + (100*i/185134));
								i++;
							}

							System.out.println(i + " record read.");
							Message m = myHandler.obtainMessage();
							Bundle b = new Bundle();
							b.putInt("end", 1);
							m.setData(b);
							myHandler.sendMessage(m);
						} catch (FileNotFoundException ex) {
						} catch (IOException ex) {
						} finally {
							try {

								System.out.println("Closing and optimizing...");
								reader.close();
								w.optimize(); 				
								w.close();
								//									runner.interrupt();

							} catch (IOException ex) {
							}
						}


					}


				};
				runner.setDaemon(true);
				runner.start();

			}//





		});


	}



	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) 
		{
		case DIALOG_PROGRESS:
			progDialog = new ProgressDialog(CreateDictionaryActivity.this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setTitle("Please Wait");
			progDialog.setMessage("Indexer process is busy");
			return progDialog;
		default:
			return null;
		}
	}

	private static void addDoc(IndexWriter w, String[] parts) throws IOException {
		Document doc = new Document();

		if (parts.length >= 2) {

			String english = StringUtils.strip(parts[0]);
			String hungarian = StringUtils.strip(parts[1]);
			doc.add(new Field("english", english, Field.Store.YES, Field.Index.ANALYZED));
			doc.add(new Field("hungarian", hungarian, Field.Store.YES, Field.Index.NOT_ANALYZED));

			if (parts.length >= 3) {
				String englishSentence = StringUtils.strip(parts[2]);
				doc.add(new Field("englishsentence", englishSentence, Field.Store.YES, Field.Index.ANALYZED));
			}

			if (parts.length >= 4) {
				String hungarianSentence = StringUtils.strip(parts[3]);
				doc.add(new Field("hungariansentence", hungarianSentence, Field.Store.YES, Field.Index.NOT_ANALYZED));
			}




		} else {
		}



		w.addDocument(doc);
	} 



	private static void addDoc(IndexWriter w, String english, String hungarian) throws IOException {
		Document doc = new Document();
		doc.add(new Field("english", english, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("hungarian", hungarian, Field.Store.YES, Field.Index.NOT_ANALYZED));


		w.addDocument(doc);
	} 

	private class MySimilarity extends DefaultSimilarity {

		@Override
		public float coord(int overlap, int maxOverlap) {

			if (overlap > 1) {
				return super.coord(overlap, maxOverlap);
			} else {
				return 0f;
			}
		}

	}

	private class MySeparatorSpinnerListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long id) {
			separator = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			separator = parent.getItemAtPosition(0).toString();

		}

	}

} 
