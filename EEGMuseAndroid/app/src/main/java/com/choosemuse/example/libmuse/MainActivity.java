package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private Button museBtn;
    private EditText nameTxt;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.nameTxt);


        nameTxt.addTextChangedListener(nameWatcher);

        museBtn = (Button) findViewById(R.id.museBtn);
        museBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = nameTxt.getText().toString();
                Intent intent = new Intent(MainActivity.this, ConnectMuseActivity.class);
                intent.putExtra("NAME",userName);
                startActivity(intent);
                finish();
            }
        });
    }

    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String userName = nameTxt.getText().toString().trim();
            museBtn.setEnabled(!userName.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}