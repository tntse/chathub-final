package edu.sfsu.csc780.chathub.ui.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

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
    private static FirebaseStorage sStorage = FirebaseStorage.getInstance();
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

    public static FirebaseRecyclerAdapter<Channel, ChannelViewHolder> getFirebaseAdapter(final Activity activity,
                                                                                         final LinearLayoutManager linearManager,
                                                                                         final RecyclerView recyclerView,
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

//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int messageCount = adapter.getItemCount();
//                int lastVisiblePosition = linearManager.findLastCompletelyVisibleItemPosition();
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (messageCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    recyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
        return adapter;
    }
}
