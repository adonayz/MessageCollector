package adonay.messagecollector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView messagesListView;
    private MessageBaseAdapter messageBaseAdapter;
    private ArrayList<String> stringArrayList = new ArrayList<>();;
    final int MY_PERMISSIONS_REQUEST_READ_SMS = 12;
    final int MESSAGES_ADDED_PER_REFRESH = 10;
    private int swipeRefreshCount = 0;
    private boolean flag_loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesListView = (ListView) findViewById(R.id.messagesListView);

        messageBaseAdapter = new MessageBaseAdapter(this, stringArrayList);
        messagesListView.setAdapter(messageBaseAdapter);

        if(this.checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        }else{
            collectSMS();
        }

        messagesListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!flag_loading)
                    {
                        flag_loading = true;
                        swipeRefreshCount++;
                        collectSMS();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void collectSMS(){
        stringArrayList.clear();

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String msgData;
            int counter = 0;
            do {
                msgData = "";
                counter++;
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx) + "\n";
                }
                stringArrayList.add(msgData);
            } while (cursor.moveToNext() && (counter < (MESSAGES_ADDED_PER_REFRESH * (swipeRefreshCount + 1))));
        }
        flag_loading = false;
        messageBaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_SMS){
            collectSMS();
        }
    }
}
