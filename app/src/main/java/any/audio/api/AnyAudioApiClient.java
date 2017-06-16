package any.audio.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ankit on 5/13/2017.
 */

public class AnyAudioApiClient {

    private static Retrofit retrofit = null;
    private static final String TAG = AnyAudioApiClient.class.getSimpleName();

    public static Retrofit getClient() {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request()
                                    .newBuilder()
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://anyaudio.in/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        return retrofit;
    }

}
