package com.mrunia.instagram.sdk;

/**
 * Created by mar on 2/15/15.
 */
public interface Callback<T> {

    public void success(T result);

    public void failure(Exception e);

    public void logout();
}
