package masterwb.design.arkcongress.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import masterwb.design.arkcongress.R;
import masterwb.design.arkcongress.entities.AutoLocation;

/**
 * Created by Master on 03/09/2016.
 */
public class AutoLocationAdapter extends ArrayAdapter<AutoLocation> {
    private GoogleApiClient googleClient;

    public AutoLocationAdapter(Context context, GoogleApiClient googleApiClient) {
        super(context, 0);
        googleClient = googleApiClient;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            holder.locationDetails = (TextView) convertView.findViewById(android.R.id.text1);
            holder.locationDetails.setTextColor(Color.BLACK);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.locationDetails.setText(getItem(position).getDescription());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if((googleClient == null) || (!googleClient.isConnected())) {
                    return null;
                }
                clear();
                displayLocationResults(constraint.toString());
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
                    if(results.count > 0)
                        notifyDataSetChanged();
                    else
                        notifyDataSetInvalidated();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private void displayLocationResults(String query) {
        LatLng firstCoord = new LatLng(16.12, -116.65);
        LatLng secCoord = new LatLng(31.56, -86.22);
        LatLngBounds bounds = new LatLngBounds(firstCoord, secCoord);

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();

        Places.GeoDataApi.getAutocompletePredictions(googleClient, query, bounds, filter)
                .setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                        if(autocompletePredictions.getStatus().isSuccess()) {
                            for(AutocompletePrediction prediction : autocompletePredictions) {
                                AutoLocation location = new AutoLocation();
                                location.setId(prediction.getPlaceId());
                                location.setDescription(prediction.getFullText(null).toString());
                                add(location);
                            }
                        }
                        autocompletePredictions.release();
                    }
                }, 20, TimeUnit.SECONDS);
    }

    public class ViewHolder {
        TextView locationDetails;
    }
}
