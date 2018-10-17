package com.starwin.app.modules;

import com.starwin.app.App;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = AppModule.class)
@Singleton
public interface AppComponent extends IComponents<App> {
    App getApp();
}
