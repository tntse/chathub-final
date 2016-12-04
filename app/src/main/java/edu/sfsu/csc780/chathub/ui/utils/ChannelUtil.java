package edu.sfsu.csc780.chathub.ui.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.Channel;

import static edu.sfsu.csc780.chathub.ui.utils.UserUtil.USER_CHILD;

/**
 * Created by david on 11/22/16.
 */

public class ChannelUtil {
    private static final String LOG_TAG = ChannelUtil.class.getSimpleName();
    public static final String CHANNELS_CHILD = "channels";
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth sFirebaseAuth = FirebaseAuth.getInstance();

    public static void createChannel(Channel channel, boolean isPublic) {
        if(isPublic) {
            sFirebaseDatabaseReference.child(CHANNELS_CHILD).child("Public").push().setValue(channel);
        } else {
            sFirebaseDatabaseReference.child(CHANNELS_CHILD).child("Private").push().setValue(channel);
        }
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView channelName;
        public static View.OnClickListener sChannelViewListener;

        public ChannelViewHolder(View view) {
            super(view);
            mView = view;
            channelName = (TextView) view.findViewById(R.id.channelNameText);
            mView.setOnClickListener(sChannelViewListener);
        }
    }

    public static FirebaseRecyclerAdapter<Channel, ChannelViewHolder> getFirebaseAdapterForChannelList(final Activity activity,
                                                                                         final View.OnClickListener clickListener) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        ChannelViewHolder.sChannelViewListener = clickListener;
        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Channel,
                ChannelViewHolder>(
                Channel.class,
                R.layout.item_channel,
                ChannelViewHolder.class,
                sFirebaseDatabaseReference.child(CHANNELS_CHILD).child("Public")) {
            @Override
            protected void populateViewHolder(final ChannelViewHolder viewHolder,
                                              Channel channel, int position) {
                viewHolder.channelName.setText(channel.getChannelName());
            }
        };

        return adapter;
    }

    public static FirebaseRecyclerAdapter<String, ChannelViewHolder> getFirebaseAdapterForUserChannelList(final Activity activity,
                                                                                                       final View.OnClickListener clickListener) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        ChannelViewHolder.sChannelViewListener = clickListener;

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<String,
                ChannelViewHolder>(
                String.class,
                R.layout.item_channel,
                ChannelViewHolder.class,
                sFirebaseDatabaseReference.child(USER_CHILD)
                        .child(UserUtil.parseUsername(preferences.getString("username", "")))
                        .child("username")
                        .child(UserUtil.parseUsername(preferences.getString("username", "")))) {
            @Override
            protected void populateViewHolder(final ChannelViewHolder viewHolder,
                                              String channel, int position) {
                viewHolder.channelName.setText(channel);
            }
        };

        return adapter;
    }

    //TODO: For some reason, it adds Random channel twice?
    public static void addUserToChannelList(Iterable<DataSnapshot> channelList, String channelName) {
        //This method is for adding a user to random's or general's userlist
        //Iterate through the list of channels
        for (DataSnapshot channel : channelList) {
            //Check if the channel name is the channel you want to update
            //Also, check if there isn't a user in the list already
            if(channel.child("channelName").getValue().equals(channelName)
                    && !channel.child("userList").getValue().toString().contains(sFirebaseAuth.getCurrentUser().getDisplayName())) {
                //Create the Map to store into firebase, the value is a String, but has to be an Object
                //because updateChildren requires it
                Map<String, Object> user = new HashMap<>();
                user.put(sFirebaseAuth.getCurrentUser().getDisplayName(), sFirebaseAuth.getCurrentUser().getDisplayName());
                //navigate to chathub/channels/{UNIQUE_KEY}/userlist
                DatabaseReference childReference = channel.child("userList").getRef();
                //updateChildren does not overwrite
                childReference.updateChildren(user);
            }
        }
    }
}
