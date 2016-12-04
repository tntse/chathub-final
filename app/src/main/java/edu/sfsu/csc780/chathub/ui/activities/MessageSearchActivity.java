package edu.sfsu.csc780.chathub.ui.activities;

import android.app.SearchManager;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.ui.fragments.FilesFragment;
import edu.sfsu.csc780.chathub.ui.fragments.MessagesFragment;
import edu.sfsu.csc780.chathub.ui.utils.FragmentTabHost;

public class MessageSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchBar);
        setSupportActionBar(toolbar);

        FragmentTabHost fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabcontent);
        fragmentTabHost.setup(this, getFragmentManager(), R.id.realtabcontent);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("messages").setIndicator("Messages"), MessagesFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("files").setIndicator("Files"), FilesFragment.class, null);
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
}
