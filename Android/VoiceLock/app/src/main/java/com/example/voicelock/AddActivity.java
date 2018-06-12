package com.example.voicelock;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    TextView voiceTxt, explainTxt;
    String address;
    Button addBtn;
    Cursor c;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        intent = getIntent();

        voiceTxt = (EditText) findViewById(R.id.voice);
        explainTxt = (EditText) findViewById(R.id.explain);
        Button addBtn = (Button) findViewById(R.id.add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(voiceTxt.getText().toString(), explainTxt.getText().toString());
            }
        });

    }

    private void save(String name, String pos) {
        DBAdapter db = new DBAdapter(this);
        //OPEN DB
        int size = 0;
        db.openDB();
        c = db.getAllPlayers();
        String mname;
        while (c.moveToNext()) {
            address = c.getString(4);
            mname = c.getString(1);
            size++;
            if (intent.hasExtra("mNAME")) {
                addBtn.setText("EDIT");
                if (mname.equals(intent.getExtras().getString("mNAME"))) {
                    db.update(name, pos, address);
                }
            }
        }

        if (!intent.hasExtra("mNAME")) {
            //COMMIT
            if (size == 0) {
                address = "00:00:00:00:00:00";
            }
                long result = db.add(name, pos, 0, address);
                if (result > 0) {
                    voiceTxt.setText("");
                    explainTxt.setText("");
                }
            }

            db.closeDB();
            Intent intent = new Intent(AddActivity.this, lockActivity.class);
            startActivity(intent);
            finish();
            //REFRESH
        }


}
