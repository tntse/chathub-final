package edu.sfsu.csc780.chathub.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.Channel;
import edu.sfsu.csc780.chathub.ui.utils.ChannelUtil;

public class CreateChannelActivity extends AppCompatActivity {

    private EditText mChannelEditText;
    private EditText mPurposeEditText;
    private TextView mChannelType;
    private TextView mChannelDescription;
    private Switch mTypeSwitch;
    private Menu mMenu;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create channel");

        mChannelDescription = (TextView)findViewById(R.id.channel_description);
        mChannelType = (TextView)findViewById(R.id.channel_type);
        mChannelEditText = (EditText) findViewById(R.id.channelEditText);
        mPurposeEditText = (EditText) findViewById(R.id.purposeEditText);
        mTypeSwitch = (Switch) findViewById(R.id.public_private_switch);
        mTypeSwitch.setChecked(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

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

        mChannelEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() < 21 && s.length() > 0 && !s.toString().contains(" ")
                        && !s.toString().contains(".") && !s.toString().matches(".*[A-Z].*")) {
                    mMenu.findItem(R.id.create_menu).setEnabled(true);
                } else {
                    mMenu.findItem(R.id.create_menu).setEnabled(false);
                }
            }
        });
    }

    private void createChannel() {
        String channelName = mChannelEditText.getText().toString();
        String channelType = mChannelType.getText().toString();
        String purpose = null;
        List<String> userList = new ArrayList<>();
        if(!mPurposeEditText.toString().equals("")) {
            purpose = mPurposeEditText.getText().toString();
        }
        //user list
        userList.add(mUser.getDisplayName());
        Log.d("Test", channelName+" "+channelType+" "+purpose+" "+mUser.getDisplayName());
        Channel channel = new Channel(userList, channelName, channelType, purpose);
        ChannelUtil.createChannel(channel);
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_activity_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        } else if (itemId == R.id.create_menu) {
            createChannel();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        super.onPrepareOptionsMenu(menu);

        MenuItem myItem = menu.findItem(R.id.create_menu);
        myItem.setEnabled(false);

        return true;
    }
}
