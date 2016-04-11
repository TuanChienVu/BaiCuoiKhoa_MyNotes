package com.vutuanchien.mynotes;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import at.markushi.ui.CircleButton;

public class MainActivity extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CircleButton btnAddNote;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST;
    Toolbar toolbar;
    TextView tvAppName;
    private Notes mDbHelper;
    SearchView mSearchview;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new Notes(this);
        mDbHelper.open();
        fillData();
        btnAddNote = (CircleButton) findViewById(R.id.btnAdd);
        tvAppName = (TextView) findViewById(R.id.tvAppName);
        mSearchview = (SearchView) findViewById(R.id.searchview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

//        set searchview full toolbar
        mSearchview.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAppName.setVisibility(View.GONE);
                mSearchview.setMaxWidth(toolbar.getWidth());
            }
        });

        mSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvAppName.setVisibility(View.VISIBLE);
                return false;
            }
        });
//        when text change, show what you want to search
        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toString().trim();
                // when the text on the searchview change, call getFilter in Adapter
                adapter.getFilter().filter(newText);
                return true;
            }
        });

//        Add more note at here
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        final Cursor notesCursor = mDbHelper.fetchAllNotes();
        String[] fromDatabase = new String[]{Notes.KEY_ROWID, Notes.KEY_TITLE, Notes.KEY_DATE};
        int[] toListView = new int[]{R.id.tvSTT, R.id.tvNoteName, R.id.tvTime};

        // Now create an array adapter and set it to display using our row
        adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.notes_row, notesCursor, fromDatabase, toListView);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return mDbHelper.fetchNotesByName(constraint.toString());
            }
        });
        setListAdapter(adapter);

    }

    //    Click here to new startactivity then create your task/ note
    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    //    Start activity with result to detail screen
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, NoteEdit.class);
        intent.putExtra(Notes.KEY_ROWID, id);
        intent.putExtra(Notes.KEY_TITLE, Notes.KEY_TITLE);
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addnote) {
            createNote();
        } else if (id == R.id.nav_search) {
            mSearchview.setMaxWidth(toolbar.getWidth());
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
