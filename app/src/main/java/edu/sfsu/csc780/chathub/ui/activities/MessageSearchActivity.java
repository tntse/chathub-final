package edu.sfsu.csc780.chathub.ui.activities;

import android.app.SearchManager;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.ui.fragments.FilesFragment;
import edu.sfsu.csc780.chathub.ui.fragments.MessagesFragment;
import edu.sfsu.csc780.chathub.ui.utils.FragmentTabHost;
import edu.sfsu.csc780.chathub.ui.utils.MessageUtil;

public class MessageSearchActivity extends AppCompatActivity {

    private static DatabaseReference sFirebaseDatabaseReference =
            FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchBar);
        setSupportActionBar(toolbar);

        final FragmentTabHost fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabcontent);
        fragmentTabHost.setup(this, getFragmentManager(), R.id.realtabcontent);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("messages").setIndicator("Messages"), MessagesFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("files").setIndicator("Files"), FilesFragment.class, null);
        setTabColor(fragmentTabHost);
        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                setTabColor(fragmentTabHost);
            }
        });

        for (int i = 0; i < fragmentTabHost.getTabWidget().getChildCount(); i++) {
            View v = fragmentTabHost.getTabWidget().getChildAt(i);

            TextView tv = (TextView) fragmentTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        searchView.setIconified(false);
        myActionMenuItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do whatever you need
                finish();
                return false;
            }
        });

        searchView.setBackgroundColor(Color.WHITE);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Search and find
                searchForMessages(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void searchForMessages(final String query) {
        sFirebaseDatabaseReference.child(MessageUtil.MESSAGES_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot channels : dataSnapshot.getChildren()) {
                    for(DataSnapshot message : channels.getChildren()) {
                        if(message.child("text").getValue().toString().contains(query)) {
                            Log.d("search", "I did it");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.unselected_tab);
        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.drawable.selected_tab);
    }
}
