package com.example.zdzyc.dateviewpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView currentdate;
    DateViewPage dateViewPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentdate = (TextView)findViewById(R.id.currentdate);
        dateViewPage = (DateViewPage) findViewById(R.id.dateviewpage);
        currentdate.setText("今天是"+dateViewPage.getmDate());
        dateViewPage.setOnChangeListener(new DateViewPage.OnChangeListener() {
            @Override
            public void onChange() {
                currentdate.setText("今天是"+dateViewPage.getmDate());
            }

            @Override
            public void tomorrow() {

            }

            @Override
            public void yesterday() {

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
}
