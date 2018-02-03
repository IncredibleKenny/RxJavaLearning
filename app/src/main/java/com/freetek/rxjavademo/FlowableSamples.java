package com.freetek.rxjavademo;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chenkai on 2018-2-3.
 */

public class FlowableSamples {

    private String TAG = "RxJavaDemo";

    private Subscription mSubscription;

    @Inject
    public FlowableSamples(){

    }

    /**
     * 同步订阅
     * @param backpressureStrategy  背压模式
     * @param isDownStreamRequest   下游是否请求
     */
    private void flowableSync(BackpressureStrategy backpressureStrategy, final boolean isDownStreamRequest){

        Flowable<Integer> upStream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();

            }
        }, backpressureStrategy);

        Subscriber<Integer> downStream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                if (isDownStreamRequest){
                    s.request(Long.MAX_VALUE);
                }
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        upStream.subscribe(downStream);
    }

    /**
     *
     * @param isDownStreamRequest ERROR模式下 下游如果不请求 则会导致上下游数据不一致 会报错MissingBackpressureException
     *
     * 上游发送第一个事件后下游就抛出了MissingBackpressureException异常, 这是因为下游没有调用request, 上游就认为下游没有处理事件的能力
     */
    public void flowableSyncRequest(boolean isDownStreamRequest){
        flowableSync(BackpressureStrategy.ERROR,isDownStreamRequest);
    }

    /**
     * 异步订阅
     * 若下游不主动请求 但是上游会继续发 但是下游不会报错
     * 下游主动请求 则发一个接收一个
     * Flowable在设计的时候采用了一种新的思路也就是响应式拉取的方式来更好的解决上下游流速不均衡的问题
     * @param isDownStreamRequest
     */
    public void flowableASyncRequest(boolean isDownStreamRequest){
        flowableASync(BackpressureStrategy.ERROR,isDownStreamRequest,false);
    }


    /**
     * 异步订阅
     * @param backpressureStrategy
     * @param isDownStreamRequest
     * @param isOutSubscription 是否使用外部Subscription
     */
    private void flowableASync(BackpressureStrategy backpressureStrategy, final boolean isDownStreamRequest, final boolean isOutSubscription){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
            }
        },backpressureStrategy).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        if (isOutSubscription){
                            mSubscription = s;
                        }
                        Log.d(TAG, "onSubscribe");
                        if (isDownStreamRequest){
                            s.request(Long.MAX_VALUE);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
    /**
     * 异步订阅
     * @param backpressureStrategy
     * @param times 上游数据发送的次数
     * @param isDownStreamRequest
     * @param isOutSubscription 是否使用外部Subscription
     */
    private void flowableASyncTimes(BackpressureStrategy backpressureStrategy, final long times, final boolean isDownStreamRequest, final boolean isOutSubscription){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < times ; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        },backpressureStrategy).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        if (isOutSubscription){
                            mSubscription = s;
                        }
                        Log.d(TAG, "onSubscribe");
                        if (isDownStreamRequest){
                            s.request(Long.MAX_VALUE);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
    /**
     * 使用外部Subscription来请求上游数据
     */
    public void flowableASyncRequestOut(){
        flowableASync(BackpressureStrategy.ERROR,false,true);
    }


    public void flowableASyncTimes(long times){
        flowableASyncTimes(BackpressureStrategy.ERROR,times,false,true);
    }
    /**
     * 手动执行request方法 请求上游数据
     * @param n 请求数据的个数
     */
    public void request(long n){
        mSubscription.request(n);
    }
}
