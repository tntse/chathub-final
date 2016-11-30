package edu.sfsu.csc780.chathub.ui.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.Channel;

/**
 * Created by david on 11/22/16.
 */

public class ChannelUtil {
    private static final String LOG_TAG = ChannelUtil.class.getSimpleName();
    public static final String CHANNELS_CHILD = "channels";
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth sFirebaseAuth;

    public static void createChannel(Channel channel) {
        sFirebaseDatabaseReference.child(CHANNELS_CHILD).push().setValue(channel);
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView channelName;
        public final ImageView icon;
        public static View.OnClickListener sChannelViewListener;

        public ChannelViewHolder(View view) {
            super(view);
            mView = view;
            channelName = (TextView) view.findViewById(R.id.channelNameText);
            icon = (ImageView) view.findViewById(R.id.icon);
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
                sFirebaseDatabaseReference.child(CHANNELS_CHILD)) {
            @Override
            protected void populateViewHolder(final ChannelViewHolder viewHolder,
                                              Channel channel, int position) {
                viewHolder.channelName.setText(channel.getChannelName());
            }
        };

        return adapter;
    }

    public static FirebaseRecyclerAdapter<Channel, ChannelViewHolder> getFirebaseAdapterForUserChannelList(final Activity activity,
                                                                                                       final View.OnClickListener clickListener) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        ChannelViewHolder.sChannelViewListener = clickListener;

        Query query = sFirebaseDatabaseReference.child(UserUtil.USER_CHILD).endAt(preferences.getString("username", ""));

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<UserChannel,
                ChannelViewHolder>(
                UserChannel.class,
                R.layout.item_channel,
                ChannelViewHolder.class,
                query.getRef().child(CHANNELS_CHILD)) {
            @Override
            protected void populateViewHolder(final ChannelViewHolder viewHolder,
                                              UserChannel channel, int position) {
                viewHolder.channelName.setText(channel.getChannelName());
            }
        };

        return adapter;
    }

    private class UserChannel {
        String channelName;

        public UserChannel (String channelName) {
            this.channelName = channelName;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }
    }
}
