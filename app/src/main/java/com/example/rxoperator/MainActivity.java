package com.example.rxoperator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.observables.MathObservable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Disposable disposable;
    private Button filter, map, from, groupBy,concat,first,last, reduce, merge, zip, min, max, count, flapmap,debunce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //create observable
        Observable<String> footballPlayerObservable = Observable.just("Messi", "Ronaldo", "Modric", "Salah", "Mbappe");
        //obsever
        Observer<String> mPlayer = getMFootballPlayerObserver();
        Observer<String> rPlayer = getRFootballPlayerObserver();
        //=====================================================
        filter.setOnClickListener(view -> {
            //observer subscribing to observable with filter operator <loc ten cau thu bat dau bang "m"/"r">
            footballPlayerObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return s.toLowerCase().startsWith("m");
                        }
                    })
                    .subscribeWith(mPlayer);

            footballPlayerObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(@NonNull String s) throws Exception {
                            return s.toLowerCase().startsWith("r");
                        }
                    }).subscribeWith(rPlayer);
        });
        from.setOnClickListener(view -> {
            Callable<String> callable = () -> {
                System.out.println("Hello World!");
                return "Hello World!";
            };
            Observable<String> observable = Observable.fromCallable(callable);

            observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(),
                    () -> System.out.println("Done"));
        });
        map.setOnClickListener(view -> {
            Observable.just(1,2,3)
                    .map(integer -> 10*integer)
                    .subscribe(integer -> {Log.d("Result map", integer+"");});
        });
        flapmap.setOnClickListener(view -> {
            Integer[] array = new Integer[10];
            for (int i = 0; i < array.length; i++) {
                array[i] = array.length-i;
            }
            Observable.just(10,20,30)
                    .flatMap(integer -> Observable.fromArray(array))
                    .subscribe(integer -> {Log.d("Result flatMap", integer+"");});
        });
        groupBy.setOnClickListener(view -> {
            Observable.just(1,2,3,4,5,6,7,8,9,10)
                    .groupBy(integer -> integer%2==0)
                    .subscribe(grouped -> grouped.toList()
                                            .subscribe(integers -> {
                                                Log.v("Result", integers + "Even(" + grouped.getKey() + ")");
                                            })
                    );
        });
        first.setOnClickListener(view -> {
            Observable.just(1,2,3,4,5,6,7,8,9,10).first(0).subscribe(integer -> {
                    Log.d("Result first" , ""+integer);
            });
        });
        last.setOnClickListener(view -> {
            Observable.just(1,2,3,4,5,6,7,8,9,10).last(0).subscribe(integer -> {
                Log.d("Result last" , ""+integer);
            });
        });
        reduce.setOnClickListener(view -> {
            footballPlayerObservable.reduce((s, s2) ->  s+s2).subscribe(s -> {
                Log.d("Result reduce" , s);
            });
        });
        concat.setOnClickListener(view -> {
            Observable<String> a= Observable.just("1","2","mmm");
            Observable<String> b= Observable.just("a","b","llll");
            Observable.concat(a,b).subscribe(s -> {
                Log.d("Result concat" , s);
            });
        });
        merge.setOnClickListener(view -> {
            Observable<String> a= Observable.just("1","2","mmm");
            Observable<String> b= Observable.just("a","b","c");
            Observable.merge(a,b).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        Log.d("Result merge" , s);
                    });
        });
        zip.setOnClickListener(view -> {
            List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
            Observable<Integer> a = Observable.fromIterable(list);
            List<String> list2 = new ArrayList<>(Arrays.asList("a","b","c"));
            Observable<String> b = Observable.fromIterable(list2);
            Observable.zip(a, b, (integer, s) -> integer+"-"+s)
            .subscribe(s -> {
                Log.d("Result zip" , s);
            });
        });
        count.setOnClickListener(view -> {
            Observable<Integer> observable = Observable.just(2, 30, 22, 5, 60, 1);
            observable.count().subscribe(l -> {
                Log.d("Result count" , ""+l);
            });
        });
    }
    private void init(){
        filter=findViewById(R.id.filter);
        map=findViewById(R.id.map);
        reduce=findViewById(R.id.reduce);
        groupBy=findViewById(R.id.groupby);
        first=findViewById(R.id.first);
        from=findViewById(R.id.from);
        last=findViewById(R.id.last);
        concat=findViewById(R.id.concat);
        merge=findViewById(R.id.merge);
        zip=findViewById(R.id.zip);
        min=findViewById(R.id.min);
        max=findViewById(R.id.max);
        count=findViewById(R.id.count);
        flapmap=findViewById(R.id.flatMap);
        debunce=findViewById(R.id.debounce);
    }
    private Observer<String> getMFootballPlayerObserver(){
        return new Observer<String>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d("TaG","onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("TaG","Name: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("TaG","onError: "+ e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("TaG","All");
            }
        };
    }
    private Observer<String> getRFootballPlayerObserver(){
        return new Observer<String>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d("TaG","onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d("TaG","Name: "+s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("TaG","onError: "+ e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("TaG","All");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}