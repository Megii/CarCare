package com.drivecom.notifications;


public class NotificationsPresenter implements NotificationsPresenterInterface{

    private NotificationsFragmentInterface notificationsView;

    public NotificationsPresenter (NotificationsFragmentInterface notificationsView){
        this.notificationsView = notificationsView;
    }
}
