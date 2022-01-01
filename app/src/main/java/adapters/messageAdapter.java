package adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import models.MessageModel;

public class messageAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> msgData;
    Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    final int SENDER_VIEWHOLDER = 0;
    final int RECEIVER_VIEWHOLDER = 1;


    public messageAdapter(ArrayList<MessageModel> msgData, Context context) {

        this.msgData = msgData;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {

        if (msgData.get(position).getuId().equals(firebaseAuth.getUid()))
            return SENDER_VIEWHOLDER;
        else
            return RECEIVER_VIEWHOLDER;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEWHOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_listitem, parent, false);
            return new OutgoingViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_listitem, parent, false);
            return new IncomingViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getClass() == OutgoingViewholder.class) {
            ((OutgoingViewholder) holder).outgoingMsg.setText(msgData.get(position).getMsgText());

            long time = msgData.get(position).getMsgTime();
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            final String timeString =
                    new SimpleDateFormat("HH:mm").format(cal.getTime());

            ((OutgoingViewholder) holder).outgoingMsgTime.setText(timeString);
        } else {

            ((IncomingViewholder) holder).incomingMsg.setText(msgData.get(position).getMsgText());
            long time = msgData.get(position).getMsgTime();
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            final String timeString =
                    new SimpleDateFormat("HH:mm").format(cal.getTime());

            ((IncomingViewholder) holder).incomingMsgTime.setText(timeString);
        }


    }

    @Override
    public int getItemCount() {
        return msgData.size();
    }

    public class OutgoingViewholder extends RecyclerView.ViewHolder {

        TextView outgoingMsg, outgoingMsgTime;


        public OutgoingViewholder(@NonNull View itemView) {
            super(itemView);

            outgoingMsg = itemView.findViewById(R.id.outgoing_msg);
            outgoingMsgTime = itemView.findViewById(R.id.outgoing_msg_time);
        }
    }

    public class IncomingViewholder extends RecyclerView.ViewHolder {

        TextView incomingMsg, incomingMsgTime;

        public IncomingViewholder(@NonNull View itemView) {
            super(itemView);

            incomingMsg = itemView.findViewById(R.id.incoming_msg);
            incomingMsgTime = itemView.findViewById(R.id.incoming_msg_time);
        }
    }

}
