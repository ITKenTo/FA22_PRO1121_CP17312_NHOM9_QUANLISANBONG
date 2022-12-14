package com.example.football_field_management.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football_field_management.DAO.Order_PitchDao;
import com.example.football_field_management.DATABASE.RoomDatabase_DA;
import com.example.football_field_management.Entity.Order_PitchEntity;
import com.example.football_field_management.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewholder> {
    List<Order_PitchEntity> list;
    RoomDatabase_DA db;
    Context context;
    List<Order_PitchEntity> listlook;
    int currentTime,firsttime,lasttime;
    DecimalFormat formatter = new DecimalFormat("###,###,###");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String date;
    Calendar lich= Calendar.getInstance();

    public HistoryAdapter(List<Order_PitchEntity> list, Context context) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
        this.listlook = new ArrayList<>();
        this.listlook.addAll(list);
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_khachhangdatsan,parent,false);
       return new viewholder(view1);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
         Order_PitchEntity oder = list.get(position);
         db = RoomDatabase_DA.getInstance(context);
         holder.tv_fieldname.setText("Filde name: "+oder.getPitch_name());
         holder.tv_bookingdate.setText("Date: "+oder.getOrder_time());
         holder.tv_time.setText("Time: "+oder.getStart_time()+" - "+oder.getEnd_time());
         holder.tv_price.setText("Total: " +formatter.format(oder.getTotal())+" VN??");
         date= sdf.format(lich.getTime());

        Log.d("DAte", date);
        if (oder.getStatus().equalsIgnoreCase("??T")) {
            holder.tv_status.setText("???? thu??");
            holder.tv_status.setTextColor(Color.GREEN);
        }else if (oder.getStatus().equalsIgnoreCase("??H")){
            holder.tv_status.setText("???? H???y");
            holder.tv_status.setTextColor(Color.RED);
        }
          currentTime = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
          firsttime= Integer.parseInt(oder.getStart_time().substring(0,2));
          lasttime= Integer.parseInt(oder.getEnd_time().substring(0,2));
         holder.imageView.setOnClickListener(view -> {
             Delete(oder,context);
         });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView tv_fieldname,tv_bookingdate,tv_price, tv_time,tv_status;
        ImageView imageView;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            tv_fieldname =itemView.findViewById(R.id.tv_history_name);
            tv_bookingdate=itemView.findViewById(R.id.tv_history_date);
            tv_price=itemView.findViewById(R.id.tv_history_price);
            tv_time=itemView.findViewById(R.id.tv_history_time);
            imageView = itemView.findViewById(R.id.image_delete);
            tv_status=itemView.findViewById(R.id.tv_status_time);
        }
    }

    public void Delete(Order_PitchEntity oder, Context context) {
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        builder.setTitle("Th??ng b??o");
        builder.setMessage("B???n c?? mu???n c?? mu???n h???y ?????t s??n");


        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (firsttime-1==currentTime && oder.getOrder_time().equalsIgnoreCase(date)) {
                    Toast.makeText(context, "S???p ?????n gi???, kh??ng th??? h???y", Toast.LENGTH_SHORT).show();
                }else if (firsttime<=currentTime && lasttime>=currentTime && oder.getOrder_time().equalsIgnoreCase(date)) {
                    Toast.makeText(context, "??ang trong gi??? ???? kh??ng th??? h???y", Toast.LENGTH_SHORT).show();
                }else {
                    oder.setStatus("??H");
                    RoomDatabase_DA.getInstance(context).order_pitchDao().update(oder);
                    Toast.makeText(context, "???? h???y", Toast.LENGTH_SHORT).show();
                    list.clear();
                    list.addAll(db.order_pitchDao().getselect());
                    notifyDataSetChanged();
                }
            }
        });

        builder.setPositiveButton("H???y", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if ((charText.length() == 0)){
            list.addAll(listlook);
        }else{
            for (Order_PitchEntity order : listlook){
                if (order.getPitch_name().toLowerCase(Locale.getDefault()).contains(charText)){
                    list.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }
}

