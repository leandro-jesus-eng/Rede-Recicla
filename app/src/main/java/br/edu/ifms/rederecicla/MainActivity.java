package br.edu.ifms.rederecicla;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private MapsFragment mapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mapsFragment = new MapsFragment();
        transaction.add(R.id.container, mapsFragment, "MapsFragment");
        transaction.commitAllowingStateLoss();

        if(!checkPermission())
            requestPermission();


        /*NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        MenuItem menuItemVidro = nav.getMenu().findItem(R.id.vidro);
        menuItemVidro.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(false);
                return true;
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        createActionToCheckBoxes(R.id.papel_papelao);
        createActionToCheckBoxes(R.id.vidro);
        createActionToCheckBoxes(R.id.plastico_pet);
        createActionToCheckBoxes(R.id.tetra_pak);
        createActionToCheckBoxes(R.id.metais);
        createActionToCheckBoxes(R.id.baterias_eletronicos);
        createActionToCheckBoxes(R.id.oleo_cozinha);

        return super.onCreateOptionsMenu(menu);
    }
    private void createActionToCheckBoxes(int i) {
        LinearLayout linearLayout = findViewById(i);
        CheckBox cB = (CheckBox) linearLayout.getChildAt(0);
        cB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                applyFilter ();
            }
        });
    }

    public boolean onCheckboxClicked(MenuItem mi) {

        LinearLayout linearLayout = (LinearLayout) mi.getActionView();
        CheckBox cB = (CheckBox) linearLayout.getChildAt(0);
        if(cB.isChecked()) {
            //mi.setChecked(false);
            cB.setChecked(false);
        } else {
            //mi.setChecked(true);
            cB.setChecked(true);
        }

        applyFilter();

        return true;
    }

    private void applyFilter () {

        List<String> filters = new ArrayList<>();
        /*
          papel
          vidro
          plástico
          tetra
          metais
          baterias
          óleo
         */
        createFilter(R.id.papel_papelao, "papel" ,filters);
        createFilter(R.id.vidro, "vidro" ,filters);
        createFilter(R.id.plastico_pet, "plástico" ,filters);
        createFilter(R.id.tetra_pak, "tetra" ,filters);
        createFilter(R.id.metais, "metais" ,filters);
        createFilter(R.id.baterias_eletronicos, "baterias" ,filters);
        createFilter(R.id.oleo_cozinha, "óleo" ,filters);

        mapsFragment.filterPoints(filters);

    }

    private void createFilter (int id, String param, List<String> filters ) {

        LinearLayout linearLayout = findViewById(id);
        CheckBox cB = (CheckBox) linearLayout.getChildAt(0);
        if(cB.isChecked()) {
            filters.add(param);
        }
    }


    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);

        return (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED) ;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{INTERNET}, PERMISSION_REQUEST_CODE);
    }

    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
