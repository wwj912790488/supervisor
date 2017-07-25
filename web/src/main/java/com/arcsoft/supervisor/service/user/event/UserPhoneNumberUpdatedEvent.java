package com.arcsoft.supervisor.service.user.event;


/**
 * A event object denotes the phone number of user is updated.
 *
 * @author zw.
 */
public class UserPhoneNumberUpdatedEvent{

    private final int userId;

    private final String phoneNumber;

    public UserPhoneNumberUpdatedEvent(int userId, String phoneNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
