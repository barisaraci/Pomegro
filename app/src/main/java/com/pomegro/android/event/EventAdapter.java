package com.pomegro.android.event;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pomegro.android.R;
import com.pomegro.android.utility.VolleySingleton;
import com.pomegro.android.utility.ImageDetailsActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Event> eventList;

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = new ArrayList<>(eventList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newsfeed, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Event event = eventList.get(position);
        holder.bind(event);
        System.out.println("cok grp");
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void animateTo(ArrayList<Event> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(ArrayList<Event> newModels) {
        for (int i = eventList.size() - 1; i >= 0; i--) {
            final Event model = eventList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Event> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Event model = newModels.get(i);
            if (!eventList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Event> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Event model = newModels.get(toPosition);
            final int fromPosition = eventList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private Event removeItem(int position) {
        final Event model = eventList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Event model) {
        eventList.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Event model = eventList.remove(fromPosition);
        eventList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutMain;
        private TextView textViewName, textViewAction, textViewTimePassed, textViewTopic, textViewContent, textViewInterests, textViewLocation, textViewTime, textViewLeftTime, textViewParticipantNumber, textViewDetails;
        private Button buttonRt, buttonStar;
        private CircularImageView avatar;
        private HorizontalScrollView scrollViewImages;
        private NetworkImageView niv1, niv2, niv3, niv4, niv5, niv6;

        private ArrayList<NetworkImageView> nivList = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);

            layoutMain = (LinearLayout) itemView.findViewById(R.id.linearLayoutMain);

            scrollViewImages = (HorizontalScrollView) itemView.findViewById(R.id.scrollViewImages);

            avatar = (CircularImageView) itemView.findViewById(R.id.circularImageViewAvatar);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewAction = (TextView) itemView.findViewById(R.id.textViewAction);
            textViewTimePassed = (TextView) itemView.findViewById(R.id.textViewTimePassed);
            textViewTopic = (TextView) itemView.findViewById(R.id.textViewTopic);
            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
            textViewInterests = (TextView) itemView.findViewById(R.id.textViewInterests);
            textViewLocation = (TextView) itemView.findViewById(R.id.textViewLocation);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            textViewLeftTime = (TextView) itemView.findViewById(R.id.textViewLeftTime);
            textViewParticipantNumber = (TextView) itemView.findViewById(R.id.textViewParticipantNumber);
            textViewDetails = (TextView) itemView.findViewById(R.id.textViewDetails);

            niv1 = (NetworkImageView) itemView.findViewById(R.id.networkImageView1);
            niv2 = (NetworkImageView) itemView.findViewById(R.id.networkImageView2);
            niv3 = (NetworkImageView) itemView.findViewById(R.id.networkImageView3);
            niv4 = (NetworkImageView) itemView.findViewById(R.id.networkImageView4);
            niv5 = (NetworkImageView) itemView.findViewById(R.id.networkImageView5);
            niv6 = (NetworkImageView) itemView.findViewById(R.id.networkImageView6);

            niv1.setVisibility(View.GONE); niv2.setVisibility(View.GONE); niv3.setVisibility(View.GONE); niv4.setVisibility(View.GONE); niv5.setVisibility(View.GONE); niv6.setVisibility(View.GONE);

            nivList.add(niv1); nivList.add(niv2); nivList.add(niv3); nivList.add(niv4); nivList.add(niv5); nivList.add(niv6);

            buttonRt = (Button) itemView.findViewById(R.id.buttonRt);
            buttonStar = (Button) itemView.findViewById(R.id.buttonStar);
        }

        public void bind(final Event event) {
            textViewName.setText(event.getName());
            textViewTopic.setText(event.getTopic());
            textViewLocation.setText(event.getLocation());
            textViewParticipantNumber.setText(event.getCurAtt() + " / " + event.getMaxAtt());
            textViewContent.setText(event.getContent());

            StringBuilder sb = new StringBuilder(event.getInterests());

            int j = 0;
            while(j < sb.length()) {
                if(sb.charAt(j) == '[' || sb.charAt(j) == ']' || sb.charAt(j) == '"') {
                    sb.deleteCharAt(j);
                    j--;
                }
                j++;
            }

            String interests[] = sb.toString().split(",");

            textViewInterests.setText("");
            for(int i = 0; i < interests.length; i++) {
                textViewInterests.setText(textViewInterests.getText() + "#" + interests[i] + " ");
            }

            if(event.getType() == 1) {
                textViewAction.setText(context.getResources().getString(R.string.actionType1));
            } else if(event.getType() == 2) {
                textViewAction.setText(context.getResources().getString(R.string.actionType2));
            } else if(event.getType() == 3) {
                textViewAction.setText(context.getResources().getString(R.string.actionType3));
            } else {
                textViewAction.setText("");
            }

            if(event.getActionTime() > 7 * 24 * 60) { textViewTimePassed.setText(event.getActionTime() / (7 * 24 * 60) + context.getResources().getString(R.string.timeWeek)); }
            else if(event.getActionTime() > 24 * 60) { textViewTimePassed.setText(event.getActionTime() / (24 * 60) + context.getResources().getString(R.string.timeDay)); }
            else if(event.getActionTime() > 60) { textViewTimePassed.setText(event.getActionTime() / 60 + context.getResources().getString(R.string.timeHour)); }
            else if(event.getActionTime() >= 0){ textViewTimePassed.setText(event.getActionTime() + context.getResources().getString(R.string.timeMinute)); }
            else { textViewTimePassed.setText(""); }

            if(event.getTimeLeft() > 7 * 24 * 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / (7 * 24 * 60) + context.getResources().getString(R.string.timeWeek) + " " + (event.getTimeLeft() / (24 * 60)) % 7 + context.getResources().getString(R.string.timeDay) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
            else if(event.getTimeLeft() > 24 * 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / (24 * 60) + context.getResources().getString(R.string.timeDay) + " " + (event.getTimeLeft() / 60) % 24 + context.getResources().getString(R.string.timeHour) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
            else if(event.getTimeLeft() > 60) { textViewLeftTime.setText("( " + event.getTimeLeft() / 60 + context.getResources().getString(R.string.timeHour) + " " + event.getTimeLeft() % 24 + context.getResources().getString(R.string.timeMinute) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
            else if(event.getTimeLeft() >= 0){ textViewLeftTime.setText("( 0" + context.getResources().getString(R.string.timeHour) + " " + event.getTimeLeft() + context.getResources().getString(R.string.timeMinute) + " " + context.getResources().getString(R.string.timeLeft) + " )"); }
            else { textViewLeftTime.setText("too late :("); }


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // 24 hours
            //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US); // 12 hours with am/pm
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
            Date date = new Date();
            date.setTime(event.getEventDate() * 1000);
            textViewTime.setText(dateFormat.format(date));

            ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();

            avatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });

            for(int i = 0; i < event.getImageCount(); i++) {new NetworkImageView(context);
                nivList.get(i).setVisibility(View.VISIBLE);
                nivList.get(i).setImageUrl("http://www.berkugudur.com/narr3/images/" + event.getId() + "_" + event.getEventVersion() + "_" + i + "_s.jpg", imageLoader);
                final int currentImage = i;
                nivList.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("eventId", event.getId());
                        intent.putExtra("imageCount", event.getImageCount());
                        intent.putExtra("eventVersion", event.getEventVersion());
                        intent.putExtra("currentImage", currentImage);
                        context.startActivity(intent);
                    }
                });
            }

            if(event.getImageCount() == 0) { scrollViewImages.setVisibility(View.GONE); }

            if(event.getType() == 2 || event.getType() == 3) {
                layoutMain.setPadding(0, 0, 0, (int) (-10 * context.getResources().getDisplayMetrics().density + 0.5f));

                textViewContent.setVisibility(View.GONE);
                textViewLocation.setVisibility(View.GONE);
                textViewInterests.setVisibility(View.GONE);
                textViewTime.setVisibility(View.GONE);
                textViewLeftTime.setVisibility(View.GONE);
                textViewParticipantNumber.setVisibility(View.GONE);
                scrollViewImages.setVisibility(View.GONE);
                buttonRt.setVisibility(View.GONE);
                buttonStar.setVisibility(View.GONE);
                textViewDetails.setVisibility(View.GONE);
            } else {
                layoutMain.setPadding(0, 0, 0, (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f));

                textViewContent.setVisibility(View.VISIBLE);
                textViewLocation.setVisibility(View.VISIBLE);
                textViewInterests.setVisibility(View.VISIBLE);
                textViewTime.setVisibility(View.VISIBLE);
                textViewLeftTime.setVisibility(View.VISIBLE);
                textViewParticipantNumber.setVisibility(View.VISIBLE);
                scrollViewImages.setVisibility(View.VISIBLE);
                buttonRt.setVisibility(View.VISIBLE);
                buttonStar.setVisibility(View.VISIBLE);
                textViewDetails.setVisibility(View.VISIBLE);
            }

            layoutMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (event.isMinimize()) {
                        Intent intent = new Intent(context, EventActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                    }
                    if (event.getType() == 2 || event.getType() == 3) {
                        layoutMain.setPadding(0, 0, 0, (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f));

                        textViewContent.setVisibility(View.VISIBLE);
                        textViewLocation.setVisibility(View.VISIBLE);
                        textViewInterests.setVisibility(View.VISIBLE);
                        textViewTime.setVisibility(View.VISIBLE);
                        textViewLeftTime.setVisibility(View.VISIBLE);
                        textViewParticipantNumber.setVisibility(View.VISIBLE);
                        scrollViewImages.setVisibility(View.VISIBLE);
                        buttonRt.setVisibility(View.VISIBLE);
                        buttonStar.setVisibility(View.VISIBLE);
                        textViewDetails.setVisibility(View.VISIBLE);

                        event.setIsMinimize(true);
                    } else {
                        Intent intent = new Intent(context, EventActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}