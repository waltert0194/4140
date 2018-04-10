package fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;

/**
 * Created by monro on 3/20/2018.
 */

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = GmapFragment.class.getSimpleName();
    private GoogleMap mMap;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(34.6834, -82.8374);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //Place marker on touch
    MarkerOptions marker = null;
    private Marker mailMarker;
    Random r = new Random();
    LatLng[] messageCoord = new LatLng[10];

    //pass arguments to ComposeFragment
    private static final String argKey = "argKey";
    private boolean markerExist = false;
    private boolean mailExist = false;
    private static View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
           if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_gmap, container, false);
        } catch (android.view.InflateException e) {
        /* map is already there, just return view as it is */
        Toast.makeText(getActivity(),"Returned to Map",Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Prompt the user for permission.
        getLocationPermission();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        //disable Map Toolbar
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //get location
        mLastKnownLocation = new Location(LocationManager.GPS_PROVIDER);
        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

        if (ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bundle up the current latlng, and spin up a new Fragment with the passed arguments
                composeNoteFragmentSwitcher();
            }
        });

        //Place marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public double randomLat(double lat){ return(lat); }

            @Override
            public void onMapClick(LatLng latLng) {

                Toast.makeText(getActivity(), "You picked a Note location!", Toast.LENGTH_SHORT).show();
                markerExist = true;
                marker = new MarkerOptions();
                marker.position(latLng);
                marker.title("PostIT here!");
                //clear previously touch position
                mMap.clear();
                mMap.addMarker(marker);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                for(int x=0; x< 10; x++) {
                    mailMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(messageCoord[x]).title("Sender of Msg"));
                }
            }
        });

        leaveRandomNotes();

        Toast.makeText(getActivity(), "Tap to place a Note", Toast.LENGTH_LONG).show();
    }

    private void leaveRandomNotes() {
        for(int x=0;x<10;x++){
            double lat = .0200 * r.nextDouble() + 34.6634;
            double lon = .0200 * r.nextDouble() + 82.8174;
            messageCoord[x] = new LatLng(lat, -lon);
        }

        for(int x=0; x< 10; x++) {
            mailMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .position(messageCoord[x]).title("Sender of Msg"));
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity(),FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void composeNoteFragmentSwitcher(){
        //check for marker, if no marker is set use current location
        if (!markerExist) {
            double[] loc ={mLastKnownLocation.getLongitude(),mLastKnownLocation.getLatitude()};
            Bundle bundle = new Bundle();
            bundle.putSerializable(argKey, loc);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
        //use marker location
        else {
            double[] loc = {marker.getPosition().longitude, marker.getPosition().latitude};
            Bundle bundle = new Bundle();
            bundle.putSerializable(argKey, loc);

            Fragment fragment = new ComposeMsgFragment();
            fragment.setArguments(bundle);
            replaceFragment(fragment);
        }
    }

    public void replaceFragment(Fragment someFragment) {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
