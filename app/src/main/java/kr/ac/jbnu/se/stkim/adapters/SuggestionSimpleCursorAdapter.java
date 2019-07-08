package kr.ac.jbnu.se.stkim.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

import kr.ac.jbnu.se.stkim.util.SuggestionsDatabase;

public class SuggestionSimpleCursorAdapter
        extends SimpleCursorAdapter
{

    public SuggestionSimpleCursorAdapter(Context context, int layout, Cursor c,
                                         String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    public SuggestionSimpleCursorAdapter(Context context, int layout, Cursor c,
                                         String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {

        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);

        return cursor.getString(indexColumnSuggestion);
    }


}