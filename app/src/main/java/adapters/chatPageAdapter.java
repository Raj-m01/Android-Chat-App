package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import models.UserModel;


public class chatPageAdapter extends RecyclerView.Adapter<chatPageAdapter.ViewHolder> {

    private final ArrayList<UserModel> userData;
    Context context;
    private static OnClickListener listener;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    public chatPageAdapter(ArrayList<UserModel> userData, Context context) {
        this.userData = userData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chats_list_items, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        userData.sort(Comparator.comparing(UserModel::getRecentMsgTime).reversed());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        // Show date/time on contact list
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(userData.get(position).getRecentMsgTime());
        final String timeString1 =
                new SimpleDateFormat("dd-M-yy HH:mm").format(cal.getTime());
        final String timeString2 =
                new SimpleDateFormat("dd-M-yy HH:mm").format(new Date().getTime());
        int diff = Integer.parseInt(timeString2.substring(0, 2)) - Integer.parseInt(timeString1.substring(0, 2));

        if (diff < 1) {
            holder.recent_time.setText(timeString1.trim().substring(8));
        } else if (diff >= 1) {
            holder.recent_time.setText(timeString1.trim().substring(0, 8));
        }


        holder.chat_name.setText(userData.get(position).getUserName());
        holder.recent_message.setText(userData.get(position).getRecentMessage());

        String picUrl = userData.get(position).getProfilePic();

        Picasso.get().load(picUrl)
                .fit().centerCrop()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.profile_pic_imageview);

    }


    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView chat_name;
        private final ImageView profile_pic_imageview;
        private final TextView recent_message;
        private final TextView recent_time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_name = itemView.findViewById(R.id.chat_name);
            profile_pic_imageview = itemView.findViewById(R.id.profile_pic_imageview);
            recent_message = itemView.findViewById(R.id.recent_message);
            recent_time = itemView.findViewById(R.id.recent_time);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(userData.get(pos));
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(UserModel userdata);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        chatPageAdapter.listener = listener;
    }

}
