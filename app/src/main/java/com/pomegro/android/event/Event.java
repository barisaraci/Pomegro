package com.pomegro.android.event;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private int id, userId, maxAtt, curAtt, imageCount, eventVersion, type; // 0: created / 1: joined / 2: suggested
    private long actionTime, eventDate, timeLeft;
    private String name, topic, content, interests, location, mods;
    private boolean isMinimize;

    public Event() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(maxAtt);
        dest.writeInt(curAtt);
        dest.writeInt(imageCount);
        dest.writeInt(eventVersion);
        dest.writeInt(type);
        dest.writeLong(actionTime);
        dest.writeLong(eventDate);
        dest.writeLong(timeLeft);
        dest.writeString(name);
        dest.writeString(topic);
        dest.writeString(content);
        dest.writeString(interests);
        dest.writeString(location);
        dest.writeString(mods);
        dest.writeByte((byte) (isMinimize ? 1 : 0));
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private Event(Parcel in){
        id = in.readInt();
        userId = in.readInt();
        maxAtt = in.readInt();
        curAtt = in.readInt();
        imageCount = in.readInt();
        eventVersion = in.readInt();
        type = in.readInt();
        actionTime = in.readLong();
        eventDate = in.readLong();
        timeLeft = in.readLong();
        name = in.readString();
        topic = in.readString();
        content = in.readString();
        interests = in.readString();
        location = in.readString();
        mods = in.readString();
        isMinimize = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userID) {
        this.userId = userID;
    }

    public int getMaxAtt() {
        return maxAtt;
    }

    public void setMaxAtt(int maxAtt) {
        this.maxAtt = maxAtt;
    }

    public int getCurAtt() {
        return curAtt;
    }

    public void setCurAtt(int curAtt) {
        this.curAtt = curAtt;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getMods() {
        return mods;
    }

    public void setMods(String mods) {
        this.mods = mods;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
    }

    public boolean isMinimize() {
        return isMinimize;
    }

    public void setIsMinimize(boolean isMinimize) {
        this.isMinimize = isMinimize;
    }

}
