package br.edu.ifms.rederecicla;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.google.maps.android.data.kml.

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private KmlLayer layer;
    private LatLng latLng;
    private List<Marker> markers;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();
        // Add a marker in Sydney and move the camera
        latLng = new LatLng(-20.4628493, -55.7919269);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 12.5), 3000, null);

        /*.title("Cidade de aquidauana"));*/
        layer = null;
        try {
            layer = new KmlLayer(mMap, R.raw.pontos, getContext());
            layer.addLayerToMap();
            layer.removeLayerFromMap(); // tem que fazer isso senão ele não cria o layer.getContainers

            // new code
            markers = new ArrayList<Marker>();
            KmlContainer container = layer.getContainers().iterator().next();
            for (KmlPlacemark placemark : container.getPlacemarks()) {
                KmlPoint kmlPoint = (KmlPoint) placemark.getGeometry();
                LatLng newLatLng = new LatLng(kmlPoint.getGeometryObject().latitude, kmlPoint.getGeometryObject().longitude);
                Marker marker = mMap.addMarker( new MarkerOptions()
                        .position(newLatLng)
                        .title(placemark.getProperty("Nome")));
                markers.add(marker);
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i("Script", "marker:" + marker.getTitle());
                }
            });

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {

                    TextView tv = new TextView(getContext());
                    LinearLayout ll = new LinearLayout(getContext());
                    ll.setPadding(30, 30, 30, 30);
                    ll.setBackgroundColor(Color.WHITE);

                    String informacoes = "";
                    for (KmlContainer container : layer.getContainers()) {

                        for (KmlPlacemark placemark : container.getPlacemarks()) {

                            if (placemark.getGeometry().getGeometryType().equals("Point")) {
                                KmlPoint point = (KmlPoint) placemark.getGeometry();
                                //latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);

                                if (marker.getPosition().latitude == point.getGeometryObject().latitude && marker.getPosition().longitude == point.getGeometryObject().longitude) {

                                    if (!placemark.getProperty("Nome").equals("")) {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Nome: </b>" + placemark.getProperty("Nome") + "</font> <br>";
                                    } else {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Nome:</b> Não temos essa informação</font> <br>";
                                    }

                                    if (!placemark.getProperty("Telefone").equals("")) {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Telefone: </b>" + placemark.getProperty("Telefone") + "</font> <br>";
                                    } else {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Telefone:<b> Não temos essa informação</font> <br>";
                                    }
                                    if (!placemark.getProperty("Endereço").equals("")) {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Endereço:</b> " + placemark.getProperty("Endereço") + "</font> <br>";
                                    } else {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Enderesso:</b> Não temos essa informação</font> <br>";
                                    }
                                    if (!placemark.getProperty("Materiais").equals("")) {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"> <b>Materiais:</b>" + placemark.getProperty("Materiais") + "</font> <br>";
                                    } else {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> Materiais:</b> Não temos essa informação</font> <br>";
                                    }
                                    if (!placemark.getProperty("e_mail").equals("")) {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> E_mail: </b>" + placemark.getProperty("e_mail") + "</font>";
                                    } else {
                                        informacoes = informacoes + "<font color=\"#2E8B57\"><b> E_mail:</b> Não temos essa informação</font> <br>";
                                    }

                                }
                            }
                        }
                    }

                    tv.setText(Html.fromHtml(informacoes));
                    ll.addView(tv);
                    return ll;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    TextView tv;
                    tv = new TextView(getContext());
                    tv.setText(Html.fromHtml("<b><font color=\"#ff0000\">Teste</font></b>"));
                    return tv;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void filterPoints(List<String> filters) {
        if(filters == null) return;

        for (Marker marker : markers) {
            marker.setVisible(false);
        }

        KmlContainer container = layer.getContainers().iterator().next();
        for(String filter : filters) {
            for (KmlPlacemark placemark : container.getPlacemarks()) {
                if (filter != null && placemark.getProperty("Materiais").contains(filter)) {
                    Marker marker = getMarker(placemark);
                    marker.setVisible(true);
                } else {
                }
            }
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 12.5), 3000, null);
    }


    private Marker getMarker (KmlPlacemark placemark) {
        KmlPoint placemarkPoint = (KmlPoint) placemark.getGeometry();

        for (Marker marker : markers) {
            if (marker.getPosition().equals( placemarkPoint.getGeometryObject() ) ) {
                     return marker;
            }
        }

        return null;
    }
}