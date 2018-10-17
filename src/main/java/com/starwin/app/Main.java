package com.starwin.app;

import com.starwin.app.modules.AppModule;
import com.starwin.app.modules.DaggerAppComponent;

public class Main {

    public static void main(String[] args) {
        DaggerAppComponent.builder().appModule(new AppModule(args)).build().getApp().run();
    }

}