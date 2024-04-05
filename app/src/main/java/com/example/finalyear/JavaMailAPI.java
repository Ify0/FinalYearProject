package com.example.finalyear;

import android.os.AsyncTask;

public class JavaMailAPI  {


    private String email;

    public JavaMailAPI() {
    }
    public JavaMailAPI(String email){
        this.email = email;
    }

    public JavaMailAPI(RoutineActivity routineActivity, String email) {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
