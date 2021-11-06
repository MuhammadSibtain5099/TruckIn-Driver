package com.sibtain.truckindriver.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sibtain.truckindriver.R;
import com.sibtain.truckindriver.RideTracking;
import com.sibtain.truckindriver.model.RideRequest;
import java.util.List;
import java.util.Locale;

public class ShowRequestsAdapter extends RecyclerView.Adapter<ShowRequestsAdapter.ViewHolder> {
    Context context;
    List<RideRequest> rideRequestList;
    Geocoder mGeocoder;
    List<Address> mAddresses;
    public static String userId;

    public ShowRequestsAdapter(Context context, List<RideRequest> rideRequestList) {
        this.context = context;
        this.rideRequestList = rideRequestList;
    }

    @NonNull
    @Override
    public ShowRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_rider_requests_items,parent,false);
    return new ShowRequestsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowRequestsAdapter.ViewHolder holder, int position) {
        holder.txtPickUp.setText(getAddress(Double.valueOf(rideRequestList.get(position).getRiderPickupLat()),
                Double.valueOf(rideRequestList.get(position).getRiderPickupLng())));
        holder.txtDropOff.setText(getAddress(Double.valueOf(rideRequestList.get(position).getRiderDropOffLat()),
                Double.valueOf(rideRequestList.get(position).getRiderDropOffLng())));
        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(context, rideRequestList.get(position).getUid(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, RideTracking.class);
            intent.putExtra("uid",rideRequestList.get(position).getUid());
            userId=rideRequestList.get(position).getUid();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rideRequestList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDropOff,txtPickUp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDropOff= itemView.findViewById(R.id.txtDropOff);
            txtPickUp = itemView.findViewById(R.id.txtPickup);

        }
    }
    public String getAddress(Double lat1, Double longi1) {
        try {
            mGeocoder = new Geocoder(context, Locale.getDefault());
            mAddresses = mGeocoder.getFromLocation(lat1, longi1, 1);

            String address = mAddresses.get(0).getAddressLine(0);
            String area = mAddresses.get(0).getLocality();

            return address + " " + area;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
