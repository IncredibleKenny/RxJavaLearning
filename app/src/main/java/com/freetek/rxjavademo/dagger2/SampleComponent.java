package com.freetek.rxjavademo.dagger2;

import com.freetek.rxjavademo.MainActivity;

import dagger.Component;

/**
 * Created by Chenkai on 2018-2-3.
 */
@Component
public interface SampleComponent {

    void inject(MainActivity mainActivity);
}
