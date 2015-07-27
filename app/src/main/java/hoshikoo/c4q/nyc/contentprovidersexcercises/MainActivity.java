package hoshikoo.c4q.nyc.contentprovidersexcercises;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private static final long EVENT_ID = 378;
    String eventTitleTx;
    String descriptionTx;
    String locationTx;

    private ListView listView;
    private TextView textView;

    private static final String CALENDAR_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final EditText title = (EditText)findViewById(R.id.title);
//        final EditText description = (EditText)findViewById(R.id.description);
//        final EditText location = (EditText)findViewById(R.id.location);

        listView = (ListView)findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.textView);
//        fetchCalendars();

        fetchEvents();

//        insertEvent();

//        updateEvent();

//        Button submitButton = (Button)findViewById(R.id.submit_button);
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_INSERT)
//                        .setData(Events.CONTENT_URI)
//                        .putExtra(Events.TITLE, title.getText().toString())
//                        .putExtra(Events.DESCRIPTION, description.getText().toString())
//                        .putExtra(Events.EVENT_LOCATION, location.getText().toString())
//                        .putExtra(Intent.EXTRA_EMAIL, "hoshikooki@gmail.com");
//                startActivity(intent);
//
//
//
//            }
//        });
    }



    private void fetchCalendars() {

        Uri uri = Calendars.CONTENT_URI;
        String [] column = new String []{
                Calendars._ID,
                Calendars.ACCOUNT_NAME,
                Calendars.CALENDAR_DISPLAY_NAME,
                Calendars.OWNER_ACCOUNT
        };

        Cursor cursor = getContentResolver().query(  //getContentResolver() is making input to String
                uri,
                column,
                Calendars.ACCOUNT_NAME + " =?",
                new String [] {"hoshikooki@gmail.com"},
                null
        );

        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(Calendars._ID));
            String accountName = cursor.getString(1);
            String displayname = cursor.getString(2);
            String owner = cursor.getString(3);
            Log.v("ContentProvider", "ID: " + id +
                    ", account: " + accountName +
                    ", displayName: " + displayname +
                    ", owner: " + owner);
        }
    }

    public void fetchEvents(){
        Uri uri = Events.CONTENT_URI;
        String [] columns = new String []{
                Events._ID,
                Events.CALENDAR_ID,
                Events.ORGANIZER,
                Events.TITLE,
                Events.EVENT_LOCATION,
                Events.DESCRIPTION,
                Events.DTSTART,
                Events.DTEND,
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.JULY, 1);
        long startTime = calendar.getTimeInMillis();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2015, Calendar.JULY, 30);
        long endTime = calendar2.getTimeInMillis();

//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(System.currentTimeMillis());
        // we can try to get the time this way as well.


        String fileter = Events.CALENDAR_ID + " = ? AND " + Events.DTSTART + " > ? AND " + Events.DTSTART + " < ?";
        String [] filterArgs = new String[]{CALENDAR_ID, String.valueOf(startTime), String.valueOf(endTime)};
        String sortOrder = Events.DTSTART + " DESC LIMIT 100"; // this is limiting to 100 events on calendar

        Cursor cursor = getContentResolver().query(
                uri,
                columns,
                fileter,
                filterArgs,
                sortOrder
        );



        ListAdapter listAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String [] {Events.TITLE, Events.DTSTART},
                new int [] {android.R.id.text1, android.R.id.text2},
                0
        );

        //to add the readable time, we need to use custom adapter using code like below.
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
//        Date resultdate = new Date(yourmilliseconds);

        listView.setAdapter(listAdapter);

        long current = System.currentTimeMillis(); // return the time from the start wherever you put this code


    }

    public void insertEvent(){

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2015, Calendar.JULY, 21, 19, 0);
        long startMillis = beginTime.getTimeInMillis();


        Calendar endTime = Calendar.getInstance();
        endTime.set(2015, Calendar.JULY, 21, 22, 0);
        long endMillis = beginTime.getTimeInMillis();


        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, "Access Code");
        values.put(Events.DESCRIPTION, "Content providers");
        values.put(Events.CALENDAR_ID, CALENDAR_ID);
        values.put(Events.EVENT_TIMEZONE, "America/New_York");

        Uri uri = getContentResolver().insert(Events.CONTENT_URI, values);

        long eventId = Long.parseLong(uri.getLastPathSegment());

        textView.setText("Event ID: " + eventId);

    }

    //this method changes the event title in calendar to "Access Code 2.1 - HOLIDAY!"
    private void updateEvent() {

        ContentValues values = new ContentValues();
        values.put(Events.TITLE, "Access Code 2.1 - HOLIDAY!");

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, EVENT_ID);
        getContentResolver().update(uri, values, null, null);
    }

    //Delete the calendar event of the ID
    private void deleteEvent() {
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, EVENT_ID);
        getContentResolver().delete(uri, null, null);
    }

}
