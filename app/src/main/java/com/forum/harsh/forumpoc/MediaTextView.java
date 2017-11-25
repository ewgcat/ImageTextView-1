package com.forum.harsh.forumpoc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Harsh Rastogi on 11/25/17.
 */

public class MediaTextView extends android.support.v7.widget.AppCompatTextView {
    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();
    private List<Call> networkCalls = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Spannable s;
    private boolean showError;

    public MediaTextView(Context context) {
        super(context);
    }

    public MediaTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        showError = true;
        s = spannableFactory.newSpannable(text);
        loadImage(text);
        super.setText(s, BufferType.SPANNABLE);
    }

    //start loading images asynchronously
    private void loadImage(CharSequence text) {
        List<String> imgUrls = findUrl(text);
        for (String imgUrl : imgUrls) {
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (showError) {
                                Toast.makeText(getContext(), "Error downloading images."
                                        , Toast.LENGTH_SHORT).show();
                                showError = false;
                            }
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setImage(bmp, String.format("%s%s%s",
                                    "\\[IMG\\](", call.request().url().toString(), ")\\[/IMG\\]"));
                        }
                    });
                }
            };
            ImageLoad loader = new ImageLoad(imgUrl, callback);
            networkCalls.add(loader.load());
        }
    }

    //set images with synchronization as the ApiTask has
    // multiple threads loading multiple images
    private void setImage(Bitmap bmp, String url) {
        lock.lock();
        try {
            if (bmp == null) return;
            bmp = BitmapResize.resize(bmp, getWidth());
            ImageSpan imageSpan = new ImageSpan(getContext(), bmp);
            CharSequence text = getText();
            Range range = getRange(text, url);
            if (range == null) {
                return;
            }
            s.setSpan(imageSpan, range.getStart(), range.getEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            super.setText(s, BufferType.SPANNABLE);
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    //get the range of the url to be replaced with the image
    private synchronized Range getRange(CharSequence text, String regex) {
        Range range = null;
        Pattern patternImageTag = Pattern.compile(regex);
        Matcher tagMatcher = patternImageTag.matcher(text);
        if (tagMatcher.find()) {
            range = new Range(tagMatcher.start(), tagMatcher.end());
        }
        return range;
    }

    //find all the image urls
    private List<String> findUrl(CharSequence text) {
        List<String> imageUrlList = new ArrayList<>();
        Pattern patternImageTag = Pattern.compile("\\[IMG\\](.*)\\[/IMG]");
        Matcher tagMatcher = patternImageTag.matcher(text);
        Matcher urlMatcher;
        while (tagMatcher.find()) {
            CharSequence tagSequence = text.subSequence(tagMatcher.start(), tagMatcher.end());
            String match = tagSequence.toString();
            match = match.replace("[IMG]", "");
            match = match.replace("[/IMG]", "");
            imageUrlList.add(match);
        }
        return imageUrlList;
    }

    //cancel all the network calls
    //in case if the activity is closed
    //networks calls will keep running
    void cancelNetworkCall() {
        for (Call networkCall : networkCalls) {
            networkCall.cancel();
        }
    }

    /**
     * Network helper class
     * Using OkHttp3
     */
    class ImageLoad {

        private final String url;
        private final Callback callback;

        private ImageLoad(String url, Callback callback) {
            this.url = url;
            this.callback = callback;
        }

        private Call load() {
            OkHttpClient client = getClient();
            Request request = createRequest(url);
            Call call = client.newCall(request);
            runApiTask(call, callback);
            return call;
        }

        private OkHttpClient getClient() {
            return new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
        }

        private Request createRequest(String url) {
            Request.Builder builder = new Request.Builder()
                    .url(url);
            return builder.build();
        }

        private void runApiTask(Call call, Callback responseCallback) {
            ApiTask task = new ApiTask(responseCallback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, call);
        }
    }

    //Run the task in background parallel to each other
    static class ApiTask extends AsyncTask<Call, Void, Void> {
        private final Callback responseCallback;

        public ApiTask(Callback responseCallback) {
            this.responseCallback = responseCallback;
        }

        @Override
        protected Void doInBackground(Call... calls) {
            calls[0].enqueue(responseCallback);
            return null;
        }
    }

    static class BitmapResize {
        //resize the bitmap to match the specific width
        //here textview width is default
        public static Bitmap resize(final Bitmap bmp, final int width) {
            ExecutorService es = Executors.newCachedThreadPool();
            Future<Bitmap> future = es.submit(new Callable<Bitmap>() {
                @Override
                public Bitmap call() throws Exception {
                    float ratio = (float) bmp.getWidth() / (float) bmp.getHeight();
                    int desiredWidth = width;
                    int desiredHeight = Math.round((float) width / ratio);
                    return Bitmap.createScaledBitmap(bmp, desiredWidth, desiredHeight, true);
                }
            });
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return bmp;
            }
        }
    }

}
