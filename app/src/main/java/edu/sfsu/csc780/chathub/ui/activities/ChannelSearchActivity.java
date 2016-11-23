package edu.sfsu.csc780.chathub.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.Channel;
import edu.sfsu.csc780.chathub.ui.utils.ChannelUtil;

public class ChannelSearchActivity extends AppCompatActivity {

    private static final int REQUEST_NEW_CHANNEL = 20;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Channel, ChannelUtil.ChannelViewHolder> mFirebaseAdapter;
    private View.OnClickListener channelJoinClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Joins the selected channel here
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_search);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.channel_list_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = ChannelUtil.getFirebaseAdapter(this,
                mLinearLayoutManager,
                mMessageRecyclerView,
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
            case R.id.create_menu:
                Intent i = new Intent(this, CreateChannelActivity.class);
                startActivityForResult(i, REQUEST_NEW_CHANNEL);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
