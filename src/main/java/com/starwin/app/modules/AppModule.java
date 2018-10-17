package com.starwin.app.modules;

import com.starwin.app.App;
import com.starwin.app.executor.AppExecutors;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    private String[] commandArgs;

    private App appInstance;

    public AppModule(String[] args) {
        commandArgs = args;
    }

    public AppModule() {
    }

    @Provides
    public App provideApp() {
        if (appInstance == null) {
            appInstance = new App(this.commandArgs);
        }
        return appInstance;
    }

    @Singleton
    @Provides
    public AppExecutors provideAppExecutors() {
        return new AppExecutors();
    }
}
