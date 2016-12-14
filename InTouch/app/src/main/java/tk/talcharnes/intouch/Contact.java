package tk.talcharnes.intouch;

/**
 * Created by Tal on 12/14/2016.
 */

public class Contact {
    private String name;
    private int phone_number;
    private int contact_frequency_days;
    private int contact_frequency_during_hour;



    public int getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(int phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContact_frequency_during_hour() {
        return contact_frequency_during_hour;
    }

    public void setContact_frequency_during_hour(int contact_frequency_during_hour) {
        this.contact_frequency_during_hour = contact_frequency_during_hour;
    }

    public int getContact_frequency_days() {
        return contact_frequency_days;
    }

    public void setContact_frequency_days(int contact_frequency_days) {
        this.contact_frequency_days = contact_frequency_days;
    }
}
