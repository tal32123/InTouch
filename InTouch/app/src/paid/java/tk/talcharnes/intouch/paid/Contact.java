package tk.talcharnes.intouch.paid;

/**
 * Created by Tal on 1/9/2017.
 * Creates a contact file to be uploaded to firebase database
 */

public class Contact {
    private String name;
    private int callFrequency;
    private int textFrequency;
    private String number;
    private String messageListJsonString;
    private Long notificationTime;
    private int callCounter;
    private int textCounter;

    public Contact() {
    }

    public int getCallCounter() {
        return callCounter;
    }

    public void setCallCounter(int callCounter) {
        this.callCounter = callCounter;
    }

    public int getTextCounter() {
        return textCounter;
    }

    public void setTextCounter(int textCounter) {
        this.textCounter = textCounter;
    }

    public String getMessageListJsonString() {
        return messageListJsonString;
    }

    public void setMessageListJsonString(String messageListJsonString) {
        this.messageListJsonString = messageListJsonString;
    }

    public Long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCallFrequency() {
        return callFrequency;

    }

    public void setCallFrequency(int callFrequency) {
        this.callFrequency = callFrequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTextFrequency() {
        return textFrequency;
    }

    public void setTextFrequency(int textFrequency) {
        this.textFrequency = textFrequency;
    }


}
