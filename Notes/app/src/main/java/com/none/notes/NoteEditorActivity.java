package com.none.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class NoteEditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String filter;
    private String oldText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editor =(EditText) findViewById(R.id.editText);
        Intent intent = getIntent();
        Uri uri =intent.getParcelableExtra(NotesProvider.CONTENT_TYPE);
        if(uri == null)
        {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        }
        else
        {
            action = Intent.ACTION_EDIT;
            filter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,DBOpenHelper.ALL_COLUMNS,filter,null,null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_note_editor, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                afterEdit();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,filter,null);
        Toast.makeText(this,"Note Deleted",Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void afterEdit()
    {
        String text = editor.getText().toString().trim();
        switch (action)
        {
            case Intent.ACTION_INSERT:
                if(text.length()==0 )
                    setResult(RESULT_CANCELED);
                else insertNote(text);
                break;
            case Intent.ACTION_EDIT:
                if(text.length()==0 ) deleteNote();
                else if (oldText.equals(text))
                    setResult(RESULT_CANCELED);
                else updateNote(text);

        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI,values,filter,null);
        Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed()
    {
        afterEdit();
    }
}
