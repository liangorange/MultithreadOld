package com.example.johnnyliang.multithread;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    private List<String> evenLoad = new ArrayList<String>();
    private List<String> oddLoad = new ArrayList<String>();

    private String evenFile = "evens.txt";
    private String oddFile = "odds.txt";

    private ListAdapter evenAdapter;
    private ListAdapter oddAdapter;

    private ListView evenListView;
    private ListView oddListView;

    private FileOutputStream evenOut;
    private FileOutputStream oddOut;

    private boolean loaded = false;

    private boolean stopped = false;

    private Thread loadThread;

    LoadContent load = new LoadContent();

    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //if (stopped) {
                loadList();
            //}
        }
    };

    Handler createHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            createFile();
            writeFile();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        evenAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, evenLoad);
        evenListView = (ListView) findViewById(R.id.evenList);

        oddAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oddLoad);
        oddListView = (ListView) findViewById(R.id.oddList);

        load.setActivity(this);
    }

    /**
     * createFiles function
     * This function will be triggered when the user click the Create File button
     * @param view
     */
    public void createFiles(View view) {
        Thread myThread = new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    System.out.println("Start writing file");
                    Thread.sleep(3000);
                    createHandler.sendEmptyMessage(0);
                    System.out.println("End writing files");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        myThread.start();
    }

    /**
     * createFile function
     * This function will create two internal files. One for even numbers,
     * another for odd numbers
     */
    public void createFile() {
        try {
            evenOut= openFileOutput(evenFile, MODE_PRIVATE);
            oddOut = openFileOutput(oddFile, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * writeFile function
     * This function will write the first 10 even and odd numbers to
     * two different files.
     */
    public void writeFile() {
        String[] evenArray = new String[10];
        int evenIndex = 0;
        for (int i = 2; i <= 20; i += 2) {
            evenArray[evenIndex++] = "" + i;
        }

        String[] oddArray = new String[10];
        int oddIndex = 0;
        for (int i = 1; i <= 20; i += 2)
            oddArray[oddIndex++] = "" + i;

        try {
            OutputStreamWriter outputWriter = new OutputStreamWriter(evenOut);
            OutputStreamWriter oddWriter = new OutputStreamWriter(oddOut);

            for (int i = 0; i < 10; i++) {
                outputWriter.write(evenArray[i]);
                outputWriter.write("\n");
            }
            outputWriter.close();

            for (int i = 0; i < 10; i++) {
                oddWriter.write(oddArray[i]);
                oddWriter.write("\n");
            }

            oddWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadFiles function
     * This function will be triggered when the user click the Load File
     * button. Once it's triggered, it will start loading numbers to the
     * ListView
     * @param view
     */
    public void loadFiles(View view) {
        //stopped = false;
        loadList();

        loadThread = new Thread(load);
        loadThread.start();

    }

    /**
     * loadList function
     * This function will assign the ArrayAdapter to the ListView in
     * order for numbers to be displayed
     */
    public void loadList() {
        // Clear the ListView for even numbers
        if (loaded) {
            evenLoad.clear();
            evenListView.setAdapter(null);
            evenListView.setAdapter(evenAdapter);

            // Clear the ListView for odd numbers
            oddLoad.clear();
            oddListView.setAdapter(null);
            oddListView.setAdapter(oddAdapter);
            loaded = false;
        }
        else {
            evenListView.setAdapter(evenAdapter);
            oddListView.setAdapter(oddAdapter);
        }
    }

    /**
     * loadAndParse function
     * This function will start loading even and odd numbers from the file
     * , then have the handler handle the display to the ListView
     */
    public void loadAndParse() {
        if (!loaded) {
            try {
                FileInputStream fileInput = openFileInput(evenFile);
                InputStreamReader InputRead = new InputStreamReader(fileInput);

                BufferedReader bf = new BufferedReader(InputRead);
                String receiveString = "";

                String[] loadArray = new String[10];

                int loadIndex = 0;

                while ((receiveString = bf.readLine()) != null) {
                    evenLoad.add(receiveString);
                    // The handler will send message back every time when one number gets loaded
                    loadHandler.sendEmptyMessage(0);
                    Thread.sleep(100);
                }

                InputRead.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            try {
                FileInputStream fileInput = openFileInput(oddFile);
                InputStreamReader InputRead = new InputStreamReader(fileInput);

                BufferedReader bf = new BufferedReader(InputRead);
                String receiveString = "";

                String[] loadArray = new String[10];

                int loadIndex = 0;
                while (!stopped && (receiveString = bf.readLine()) != null) {
                    oddLoad.add(receiveString);
                    loadHandler.sendEmptyMessage(0);
                    Thread.sleep(300);
                }

                loaded = true;

                InputRead.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLoading(View view) {
        stopped = true;
        load.setStop(true);
    }

    public boolean getStop() {
        return stopped;
    }

    private synchronized void stopThread(Thread theThread)
    {
        if (theThread != null)
        {
            theThread = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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
