package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.cpsc41400.a4140app.R;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by monro on 3/27/2018.
 */

public class ComposeMsgFragment extends Fragment {

    private AdapterView.OnItemClickListener onContactClickListener;
    private static final String argKey = "argKey";
    private Button updateLocBtn;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

        double[] loc = getArguments().getDoubleArray(argKey);
        final String lat = Double.toString(loc[1]);
        final String lng = Double.toString(loc[0]);


        updateLocBtn = rootView.findViewById(R.id.changeLocBtn);
        String latlng = lat + " : " +lng;
        updateLocBtn.setText(latlng);
        updateLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =  new GmapFragment();
                replaceFragment(fragment);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView actv = getView().findViewById(R.id.whoNo);
        actv.setOnItemClickListener(onContactClickListener);





    }

    public void replaceFragment(Fragment someFragment) {
   android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.content_frame, someFragment);
    transaction.addToBackStack(null);
    transaction.commit();
}







}