package com.thoughtworks.pumpkin;

import android.app.ProgressDialog;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thoughtworks.pumpkin.adapter.BooksCursor;
import com.thoughtworks.pumpkin.helper.Util;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.List;

@ContentView(R.layout.books)
public class BrowseActivity extends AbstractParseActivity {

    @InjectView(R.id.books)
    ListView books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String category = getIntent().getExtras().getString("category");
        ParseQuery query = new ParseQuery("Book");
        ParseQuery innerQuery = new ParseQuery("Category");
        innerQuery.whereEqualTo("name", category);
        query.whereMatchesQuery("parent", innerQuery);
        query.orderByAscending("rating");
        final BrowseActivity browseActivity = this;
        final ProgressDialog dialog = Util.showProgressDialog(this);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (dialog.isShowing()) dialog.dismiss();
                MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "bookImage", "Title"});
                for (int i = 0; i < parseObjects.size(); i++) {
                    cursor.addRow(new Object[]{i, parseObjects.get(i).getString("thumbnail"), parseObjects.get(i).getString("title")});
                }
                books.setAdapter(new BooksCursor(browseActivity, R.layout.book, cursor,
                        new String[]{"bookImage", "title"}, new int[]{R.id.bookImage, R.id.title}));
            }
        });
    }
}
