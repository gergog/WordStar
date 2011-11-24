package firstapp.wirinun.db;

import java.util.ArrayList;

import firstapp.wirinun.Word;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import firstapp.wirinun.Lesson;

public class DBHelper {

    public static final String DB_NAME = "db_words";
    public static final String DB_TABLE_MAIN = "db_table_words";
    public static final String DB_TABLE_LESSON = "db_table_lessons";
    public static final int DB_VERSION = 2;
    private static final String CLASSNAME = DBHelper.class.getSimpleName();
    private static final String[] WORD_COLS = new String[]{"_id", "english", "hungarian", "engSent", "noOfAsked",
        "noOfKnown", "known", "createdAt", "lesson"};
    private static final String[] LESSON_COLS = new String[]
	    { "_id", "name", "parentId"};	
    private SQLiteDatabase db = null;
    private final DBOpenHelper dbOpenHelper;

    public DBHelper(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context, DB_NAME, DB_VERSION);
        this.establishDb();
        
        
        
    }

    public void establishDb() {
        if (this.db == null) {
            Log.v("DBHelper", "Database established");
            this.db = this.dbOpenHelper.getWritableDatabase();
        }
    }

    public void cleanup() {
        if (this.db != null) {
            Log.v("DBHelper", "Database closed");
            this.db.close();
            this.db = null;
        }
    }

    public void insert(Word word) {
        ContentValues values = new ContentValues();

        Log.v("DBHelper", "english : " + word.getEnglish());
        Log.v("DBHelper", "hungarian : " + word.getHungarian());
        Log.v("DBHelper", "example : " + word.getEnglishSentence());
        Log.v("DBHelper", "lesson : " + word.getLessonId());
        
        values.put("english", word.getEnglish());
        values.put("hungarian", word.getHungarian());
        values.put("engSent", word.getEnglishSentence());
        values.put("noOfAsked", word.getNoAsked());
        values.put("noOfKnown", word.getNoKnown());
        values.put("known", word.isKnown());
        values.put("noOfAsked", word.getNoAsked());
        values.put("lesson", word.getLessonId());
//		values.put("createdAt", word.getCreatedAt());

        establishDb();
        this.db.insert(DBHelper.DB_TABLE_MAIN, null, values);
        cleanup();
        
    }

    public void update(Word word) {
        ContentValues values = new ContentValues();

        values.put("english", word.getEnglish());
        values.put("hungarian", word.getHungarian());
        values.put("engSent", word.getEnglishSentence());
        values.put("noOfAsked", word.getNoAsked());
        values.put("noOfKnown", word.getNoKnown());
        values.put("known", word.isKnown());
        values.put("noOfAsked", word.getNoAsked());
        values.put("lesson", word.getLessonId());
//		values.put("createdAt", word.getCreatedAt());

        establishDb();
        this.db.update(DBHelper.DB_TABLE_MAIN, values, "_id=" + word.getId(), null);
        cleanup();
    }

    public void deleteWord(long id) {
        establishDb();
        this.db.delete(DBHelper.DB_TABLE_MAIN, "_id=" + id, null);
        cleanup();
    }

    public void deleteWord(String english) {
        establishDb();
        this.db.delete(DBHelper.DB_TABLE_MAIN, "english='" + english + "'", null);
        cleanup();
    }

    public Word getWord(String english) {
        Cursor c = null;
        Word word = null;
        establishDb();
        try {
            c = this.db.query(true, DBHelper.DB_TABLE_MAIN, DBHelper.WORD_COLS,
                    "english = '" + english + "'", null, null, null, null,
                    null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                word = new Word();
                word.setId(c.getLong(0));
                word.setEnglish(c.getString(1));
                word.setHungarian(c.getString(2));
                word.setEnglishSentence(c.getString(3));
                word.setNoAsked(c.getInt(4));
                word.setNoKnown(c.getInt(5));
                word.setKnown((c.getInt(6) == 1 ? true : false));
//				word.setCreatedAt(new java.util.Date(c.getString(8)));
                word.setLessonId(c.getLong(8));
            }
        } catch (SQLException e) {
            Log.v("DBHelper", DBHelper.CLASSNAME, e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return word;
    }

    public ArrayList<Word> getAllWord() {
        ArrayList<Word> ret = new ArrayList<Word>();
        Cursor c = null;
        establishDb();
        try {
            c = this.db.query(DBHelper.DB_TABLE_MAIN, DBHelper.WORD_COLS, null,
                    null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Word word = new Word();
                word.setId(c.getLong(0));
                word.setEnglish(c.getString(1));
                word.setHungarian(c.getString(2));
                word.setEnglishSentence(c.getString(3));
                word.setNoAsked(c.getInt(4));
                word.setNoKnown(c.getInt(5));
                word.setKnown((c.getInt(6) == 1 ? true : false));
//				word.setCreatedAt(new java.util.Date(c.getString(8)));
                word.setLessonId(c.getLong(8));
                ret.add(word);
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.v("Hello", DBHelper.CLASSNAME, e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return ret;
    }

    public ArrayList<Word> getAllWordinLesson(long id) {

        ArrayList<Word> ret = new ArrayList<Word>();
//                int lessonId = -1;

//                if (!name.isEmpty()) {
        // we need to get the lesson id here
//                }
        establishDb();

        Cursor c = null;
        try {
            c = this.db.query(DBHelper.DB_TABLE_MAIN, DBHelper.WORD_COLS, "lesson = '" + id + "'",
                    null, null, null, null);
            int numRows = c.getCount();
            Log.v("DBHelper", numRows + " words found" );
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Word word = new Word();
                word.setId(c.getLong(0));
                word.setEnglish(c.getString(1));
                word.setHungarian(c.getString(2));
                word.setEnglishSentence(c.getString(3));
                word.setNoAsked(c.getInt(4));
                word.setNoKnown(c.getInt(5));
                word.setKnown((c.getInt(6) == 1 ? true : false));
//				word.setCreatedAt(new java.util.Date(c.getString(8)));
                word.setLessonId(c.getLong(8));
                ret.add(word);
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.v("DBHelper", DBHelper.CLASSNAME, e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return ret;
    }

    // Lesson
    
    
        
        
	public void insert(Lesson lesson) {
		ContentValues values = new ContentValues();
	
                
                // TASK : id generation
                // generated automatically
                               
		values.put("name", lesson.getName());
		values.put("parentId", lesson.getParentId());
		
		establishDb();
		this.db.insert(DBHelper.DB_TABLE_LESSON, null, values);
                cleanup();
                Log.v("DBHelper", "Lesson inserted");
	}
        
	public void update(Lesson lesson) {
		ContentValues values = new ContentValues();
		
		values.put("name", lesson.getName());
		values.put("parentId", lesson.getParentId());

                establishDb();
		this.db.update(DBHelper.DB_TABLE_LESSON, values, "_id=" + lesson.getId(), null);
                cleanup();
	}
        
	public void deleteLessonWithWords(long id) {
                establishDb();
		this.db.delete(DBHelper.DB_TABLE_MAIN, "lesson=" + id, null);               
		this.db.delete(DBHelper.DB_TABLE_LESSON, "_id=" + id, null);
                cleanup();
	}
        
	public void deleteLessonWords(long id) {
                establishDb();
		this.db.delete(DBHelper.DB_TABLE_MAIN, "lesson=" + id, null);               
                cleanup();
	}
        
        
	public void deleteLesson(long id) {
                establishDb();
		this.db.delete(DBHelper.DB_TABLE_LESSON, "_id=" + id, null);
                cleanup();
	}
        
	public void deleteLesson(String name) {
                establishDb();
		this.db.delete(DBHelper.DB_TABLE_LESSON, "name='" + name + "'", null);
                cleanup();
                
	}
	
        
        
        private String getLessonName(long id) {
            Lesson l = getLesson(id);
            
            if (l != null) {
                return l.getName();
            } else {
                return "";
            }
            
        }
        
        private long getLessonId(String name) {
            
            Lesson l = getLesson(name);
            
            if (l != null) {
                return l.getId();
            } else {
                return -1;
            }
        }
        
	public Lesson getLesson(long id) {
		Cursor c = null;
		Lesson lesson = null;
                establishDb();
		try {
			c = this.db.query(true, DBHelper.DB_TABLE_LESSON, DBHelper.LESSON_COLS,
				"_id = '" + id + "'", null, null, null, null,
				null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				lesson = new Lesson();
				lesson.setId(c.getLong(0));
				lesson.setName(c.getString(1));
				lesson.setParentId(c.getLong(2));
			}
		} catch (SQLException e) {
			Log.v("DBHelper", DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return lesson;
	}
        
        
	public Lesson getLesson(long parentId, String name) {
		Cursor c = null;
		Lesson lesson = null;
                establishDb();

                try {
			c = this.db.query(true, DBHelper.DB_TABLE_LESSON, DBHelper.LESSON_COLS,
				"name = '" + name + "' AND parentId '" + parentId + "'", null, null, null, null,
				null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				lesson = new Lesson();
				lesson.setId(c.getLong(0));
				lesson.setName(c.getString(1));
				lesson.setParentId(c.getLong(2));
			}
		} catch (SQLException e) {
			Log.v("DBHelper", DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return lesson;
	}
        
        
        // this return with more result,notuseful
	public Lesson getLesson(String name) {
		Cursor c = null;
		Lesson lesson = null;
                establishDb();
		try {
			c = this.db.query(true, DBHelper.DB_TABLE_LESSON, DBHelper.LESSON_COLS,
				"name = '" + name + "'", null, null, null, null,
				null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				lesson = new Lesson();
				lesson.setId(c.getLong(0));
				lesson.setName(c.getString(1));
				lesson.setParentId(c.getLong(2));
			}
		} catch (SQLException e) {
			Log.v("DBHelper", DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return lesson;
	}

        
        
        
	public ArrayList<Lesson> getAllLesson() {
		ArrayList<Lesson> ret = new ArrayList<Lesson>();
		Cursor c = null;
                establishDb();
		try {
			c = this.db.query(DBHelper.DB_TABLE_LESSON, DBHelper.LESSON_COLS, null,
				null, null, null, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Lesson lesson = new Lesson();
				lesson.setId(c.getLong(0));
				lesson.setName(c.getString(1));
				lesson.setParentId(c.getLong(2));
				ret.add(lesson);
				c.moveToNext();
			}
		} catch (SQLException e) {
			Log.v("DBHelper", DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return ret;
	}			

	public ArrayList<Lesson> getAllLessoninLesson(long id) {
            
		ArrayList<Lesson> ret = new ArrayList<Lesson>();
//                long lessonId = 0;
                
//                if (!name.isEmpty()) {
                    // we need to get the lesson id here
//                    lessonId= getId(name);
//                }
                
		Cursor c = null;
                establishDb();
		try {
			c = this.db.query(DBHelper.DB_TABLE_LESSON, DBHelper.LESSON_COLS, "parentId = '" + id + "'",
				null, null, null, null);
			int numRows = c.getCount();
                        Log.v("DBHelper", numRows + " lesson found" );
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
                                Lesson lesson = new Lesson();
				lesson.setId(c.getLong(0));
				lesson.setName(c.getString(1));
				lesson.setParentId(c.getLong(2));
				ret.add(lesson);

				c.moveToNext();
			}
		} catch (SQLException e) {
			Log.v("DBHelper", DBHelper.CLASSNAME, e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return ret;
	}			

        
        
    
    
    
    private static class DBOpenHelper extends SQLiteOpenHelper {

        private static final String DB_CREATE_WORD = "CREATE TABLE "
                + DBHelper.DB_TABLE_MAIN
                + " (_id INTEGER PRIMARY KEY, english TEXT UNIQUE NOT NULL,"
                + "hungarian TEXT, engSent TEXT, hunSent TEXT, noOfAsked INTEGER, "
                + " noOfKnown INTEGER, known BOOLEAN, createdAt DATE, lesson INTEGER);";
		private static final String DB_CREATE_LESSON = "CREATE TABLE "
			+ DBHelper.DB_TABLE_LESSON
			+ " (_id INTEGER PRIMARY KEY, name TEXT NOT NULL,"
			+ " parentId INTEGER); ";

        public DBOpenHelper(Context context, String dbName, int version) {
            super(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
            Log.v("DBHelper", "DBOpenHelper called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.v("DBHelper", "Word table created");
                db.execSQL(DBOpenHelper.DB_CREATE_WORD);
                db.execSQL(DBOpenHelper.DB_CREATE_LESSON);
            } catch (SQLException e) {
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Log.v("DBHelper", "Database opened");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                int newVersion) {
            Log.v("DBHelper", "Database upgraded");
            db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_TABLE_MAIN);
            db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_TABLE_LESSON);
            this.onCreate(db);
        }

    }
}
