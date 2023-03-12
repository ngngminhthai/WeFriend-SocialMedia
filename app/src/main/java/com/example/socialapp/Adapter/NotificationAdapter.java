package com.example.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialapp.Model.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.Model.Notification;
import com.example.socialapp.R;
import com.example.socialapp.databinding.NotificationRvDesignBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>  {

    ArrayList<Notification> list;
    Context context;

    public NotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification notification = list.get(position);

        String type = notification.getType();

        // Chuyển từ Time Mili thành Time Ago
        String result = "";
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - notification.getNotificationAt();

        long second = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hour = TimeUnit.MILLISECONDS.toHours(diff);
        long day = TimeUnit.MILLISECONDS.toDays(diff);

        if (day > 0) {
            result = day + " ngày trước";
        } else if (hour > 0) {
            result = hour + " giờ trước";
        } else if (minute > 0) {
            result = minute + " phút trước";
        } else {
            result = second + " giây trước";
        }

        holder.binding.time.setText(result);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.notificationProfile);

                        if (type.equals("like")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() +"</b>"+ " liked your post"));
                        }else if (type.equals("comment")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() +"</b>"+ " Commented on your post"));
                        }else {
                            holder.binding.notification.setText(Html.fromHtml("<b>" +user.getName()+"</b>" + " start following you."));
                            System.out.println(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Chờ Activity Comment
//        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!type.equals("follow")){
//
//                    FirebaseDatabase.getInstance().getReference()
//                            .child("notification")
//                            .child(notification.getPostedBy())
//                            .child(notification.getNotificationID())
//                            .child("checkOpen")
//                            .setValue(true);
//                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                    Intent intent = new Intent(context, CommentActivity.class);
//                    intent.putExtra("postId", notification.getPostID());
//                    intent.putExtra("postedBy", notification.getPostedBy());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//
//            }
//        });
        Boolean checkOpen = notification.isCheckOpen();
        if (checkOpen == true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else {

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        NotificationRvDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRvDesignBinding.bind(itemView);
        }
    }
}
