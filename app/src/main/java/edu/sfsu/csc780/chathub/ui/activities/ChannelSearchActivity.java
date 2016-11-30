package edu.sfsu.csc780.chathub.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.Channel;
import edu.sfsu.csc780.chathub.ui.utils.ChannelUtil;
import edu.sfsu.csc780.chathub.ui.utils.UserUtil;

public class ChannelSearchActivity extends AppCompatActivity {

    private static final int REQUEST_NEW_CHANNEL = 20;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private FirebaseRecyclerAdapter<Channel, ChannelUtil.ChannelViewHolder> mFirebaseAdapter;
    private View.OnClickListener channelJoinClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            final TextView channelName = (TextView) view.findViewById(R.id.channelNameText);
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ChannelSearchActivity.this);
            //Joins the selected channel here

            //Adds to user's channels list
            sFirebaseDatabaseReference.child(UserUtil.USER_CHILD).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot user : dataSnapshot.getChildren()) {
                        //If this is the correct user
                        //and this user doesn't have the channel already in their channel list
                        if(user.getKey().equals(sp.getString("username", "")) &&
                                !user.child("username").child(sp.getString("username", "")).toString().contains(channelName.getText().toString())) {
                            DatabaseReference channelList = user.child("username").child(sp.getString("username", "")).getRef();
                            Map<String, Object> channel = new HashMap<>();
                            channel.put(channelName.getText().toString(), channelName.getText().toString());
                            channelList.updateChildren(channel);
                        }
                    }
                    sFirebaseDatabaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            //Add to channel's user list
            sFirebaseDatabaseReference.child(ChannelUtil.CHANNELS_CHILD).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChannelUtil.addUserToChannelList(dataSnapshot.getChildren(), channelName.getText().toString());
                    sFirebaseDatabaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            //Change view to channel message view
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_search);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Channels");

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.channel_list_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = ChannelUtil.getFirebaseAdapterForChannelList(this,
                channelJoinClickListener);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.create_menu:
                Intent i = new Intent(this, CreateChannelActivity.class);
                startActivityForResult(i, REQUEST_NEW_CHANNEL);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_NEW_CHANNEL) {
            if(resultCode == Activity.RESULT_OK) {
                //TODO: Main Activity has to change layout to show changed channel
                finish();
            }
        }
    }
}
