package edu.sfsu.csc780.chathub.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import edu.sfsu.csc780.chathub.R;

public class CreateChannelActivity extends AppCompatActivity {

    private EditText mChannelEditText;
    private EditText mPurposeEditText;
    private TextView mChannelType;
    private TextView mChannelDescription;
    private Switch mTypeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create channel");

        mChannelDescription = (TextView)findViewById(R.id.channel_description);
        mChannelType = (TextView)findViewById(R.id.channel_type);
        mChannelEditText = (EditText) findViewById(R.id.channelEditText);
        mPurposeEditText = (EditText) findViewById(R.id.purposeEditText);
        mTypeSwitch = (Switch) findViewById(R.id.public_private_switch);
        mTypeSwitch.setChecked(true);

        mTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mChannelType.setText(getString(R.string.public_channel));
                    mChannelDescription.setText(getString(R.string.public_description));
                } else {
                    mChannelType.setText(getString(R.string.private_channel));
                    mChannelDescription.setText(getString(R.string.private_description));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        }
        return true;
    }
}
