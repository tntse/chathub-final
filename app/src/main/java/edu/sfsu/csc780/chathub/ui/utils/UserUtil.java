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

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.User;

/**
 * Created by david on 11/22/16.
 */

public class UserUtil {
    private static final String LOG_TAG = UserUtil.class.getSimpleName();
    public static final String USER_CHILD = "users";
    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth sFirebaseAuth = FirebaseAuth.getInstance();

    public static void createUser(User user, Activity activity) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(activity);
        sFirebaseDatabaseReference.child(USER_CHILD).child(sFirebaseAuth.getCurrentUser().getDisplayName()).setValue(user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView username;
        public final ImageView icon;
        public static View.OnClickListener sChannelViewListener;

        public UserViewHolder(View view) {
            super(view);
            mView = view;
            username = (TextView) view.findViewById(R.id.username);
            icon = (ImageView) view.findViewById(R.id.active_icon);
            mView.setOnClickListener(sChannelViewListener);
        }
    }

    public static FirebaseRecyclerAdapter<User, UserViewHolder> getFirebaseAdapterForUserList(
                                                                                   final View.OnClickListener clickListener) {
        UserViewHolder.sChannelViewListener = clickListener;
        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User,
                UserViewHolder>(
                User.class,
                R.layout.item_channel,
                UserViewHolder.class,
                sFirebaseDatabaseReference.child(USER_CHILD)) {
            @Override
            protected void populateViewHolder(final UserViewHolder viewHolder,
                                              User user, int position) {
                viewHolder.username.setText((String)user.getUsername().keySet().toArray()[0]);
            }
        };

        return adapter;
    }
}
