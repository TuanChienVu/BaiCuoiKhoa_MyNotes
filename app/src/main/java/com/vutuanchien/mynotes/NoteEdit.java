package com.vutuanchien.mynotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteEdit extends AppCompatActivity implements View.OnClickListener {
    public static int numTitle = 1;
    public static String curDate = "";
    public static String curText = "";
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mDateText;
    private Long mRowId;

    FrameLayout btnBack, btnNoteContent, btnShopCart, btnCheckList;
    RelativeLayout rlTitle;
    ImageButton btnMenu;
    ImageButton btnDelete;
    ImageButton btnShare;
    ImageButton btnAdd;
    Button btnTitle;
    boolean showTitle = true;
    private Cursor cursor;
    private Notes mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new Notes(this);
        mDbHelper.open();
        setContentView(R.layout.activity_noteedit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDateText = (TextView) findViewById(R.id.notelist_date);

        btnBack = (FrameLayout) findViewById(R.id.btnBack);
        btnCheckList = (FrameLayout) findViewById(R.id.btnCheckNote);
        btnShopCart = (FrameLayout) findViewById(R.id.btnShopCart);
        btnNoteContent = (FrameLayout) findViewById(R.id.btnNoteContent);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnTitle = (Button) findViewById(R.id.btnTitle);

//        set on click for buttons
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnTitle.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

//        set time & date for your note
        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
        curDate = formatter.format(curDateTime);
        mDateText.setText("" + curDate);


//        to hide node title when it finish

        mBodyText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (showTitle == true) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_show_title);
                    Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_back_content);
                    btnTitle.setText(mTitleText.getText().toString());
                    rlTitle.startAnimation(animation);
                    mBodyText.startAnimation(animation1);
                    rlTitle.setVisibility(View.GONE);
                    showTitle = false;
                }
            }
        });

//        check your id note in database

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(Notes.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(Notes.KEY_ROWID) : null;
        }
        populateFields();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnShare) {
            share();
        } else if (id == R.id.btnDelete) {
            showDialog();
        } else if (id == R.id.btnAdd) {
            finish();
            Intent intentAdd = new Intent(this, NoteEdit.class);
            startActivity(intentAdd);
        } else if (id == R.id.btnTitle) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_hide_title);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_show_content);
            rlTitle.setVisibility(View.VISIBLE);
            rlTitle.startAnimation(animation);
            mBodyText.startAnimation(animation1);
            rlTitle.requestFocus();
            showTitle = true;
        } else if (id == R.id.btnMenu) {
            this.openOptionsMenu();
        }
    }

    //    share content of notes to others by email, messages...
    public void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mTitleText.getText().toString());
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText.getText().toString());
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_note)));
    }

    //    confirm to delete note
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.content);
        builder.setNegativeButton(R.string.yesdelete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDbHelper.deleteNote(mRowId);
                Intent intentBack = new Intent(NoteEdit.this, MainActivity.class);
                startActivity(intentBack);
            }
        });
        builder.setPositiveButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // This constructor for LayoutInflater
    public static class LineEditText extends EditText {
        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.BLUE);
        }

        private Rect mRect;
        private Paint mPaint;

        //        design background for note body
        @Override
        protected void onDraw(Canvas canvas) {

            int height = getHeight();
            int line_height = getLineHeight();
            int count = height / line_height;
            if (getLineCount() > count)
                count = getLineCount();

            Rect r = mRect;
            Paint paint = mPaint;
            int baseline = getLineBounds(0, r);

            for (int i = 0; i < count; i++) {

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();

                super.onDraw(canvas);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        btnTitle.setText(mTitleText.getText());
        saveState();
        outState.putSerializable(Notes.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnTitle.setText(mTitleText.getText());
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newnotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:

		    	/* Here is the introduce about app */
                AlertDialog.Builder dialog = new AlertDialog.Builder(NoteEdit.this);
                dialog.setTitle("About");
                dialog.setMessage("Hello! Vu Tuan Chien");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                return true;
            case R.id.delete:
                showDialog();
                return true;
            case R.id.save:
                saveState();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    save note after adding new notes
    private void saveState() {
        String title = btnTitle.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null && title.equals("") && !body.equals("")) {
            // insert with title is first paragraph of content
            title = getFirstParagraph(body);
            long id = mDbHelper.createNote(title, body, curDate);
            if (id > 0) {
                mRowId = id;
            } else {
                Log.e("saveState", "failed to create note");
            }
        } else if (mRowId == null && !title.equals("") && body.equals("")) {
            // insert with title is first paragraph of content
            long id = mDbHelper.createNote(title, body, curDate);
            if (id > 0) {
                mRowId = id;
            } else {
                Log.e("saveState", "failed to create note");
            }
        } else if (mRowId == null && !title.equals("") && !body.equals("")) {
            // insert with title is first paragraph of content
            long id = mDbHelper.createNote(title, body, curDate);
            if (id > 0) {
                mRowId = id;
            } else {
                Log.e("saveState", "failed to create note");
            }
        } else if (title.equals("") && body.equals("")) {
            // both title and body are empty
            // do nothing
            Log.d("INSERT", "nothing happen on DB");

        }
        // title not blank and content blank OR title and content are not blank
        // insert normal
        else {
            if (!mDbHelper.updateNote(mRowId, title, body, curDate)) {
                Log.e("saveState", "failed to update note");
            }
        }
    }

    // get first line in content
    public String getFirstParagraph(String content) {
        String titleTemp = "";
        if (content.length() > 20) {
            titleTemp = content.substring(0, 20);
        } else {
            titleTemp = content;
        }
        return titleTemp;
    }

    private void populateFields() {
        if (mRowId != null) {
            cursor = mDbHelper.fetchNote(mRowId);
            startManagingCursor(cursor);
            mTitleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Notes.KEY_TITLE)));
            mBodyText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Notes.KEY_BODY)));
            curText = cursor.getString(cursor.getColumnIndexOrThrow(Notes.KEY_BODY));

        }
    }
}
