# RetrofitView

`Retrofit2`是`square`公司出品的一个网络请求库，网上有很多相关的介绍。我很久以前都想去研究了，但一直都有各种事情耽搁，让我们一起去捋一捋，这篇主要讲解`Retrofit2`与`RxJava`的用法。

 1. `get`请求 （泛型处理返回结果，`HashMap`组装参数，获取返回字符串）
 2. `post`请求(包括`key/value`,以及`body`)
 3. 文件上传(进度监听)
 4. 文件下载
 5. 开启日志拦截
 6. 与RxJava配合使用

##使用前的配置

gradle 构建：

```
    compile 'com.google.code.gson:gson:2.3.1'
    compile 'com.squareup.retrofit2:retrofit:2.0.0'
    compile 'com.squareup.retrofit2:converter-gson:2.0.0'
    compile 'com.squareup.okhttp3:logging-interceptor:3.2.0'
```

新建`RestClient`构造http接口类，核心代码：
```
retrofit=new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
 myService=retrofit.create(MyService.class);
```

生成了代理类之后，就可以进行相应请求了。

##get请求

###1、新建接口类RestService

```
public interface RestService {

    @GET("/Route.axd?method=vast.sync.category.issued&format=Json")
    Call<ApiResponse<Category>> getCategory(@Query("StoreId") String storeId,
                                            @Query("Condition") String condition,
                                            @Query("LastUpdateTime") String lastUpdateTime,
                                            @Query("PageIndex") String pageIndex,
                                            @Query("PageSize") String pageSize);
                          }                  
```

需要注意`@Query`注解不能丢，即使形参和请求的`key`相同也要加上，否则报错；`@GET("")`内容为访问地址的后部分，记得我第一次理解的时候根本不知道是什么玩意。比如：`url="http://plus.366ec.net/Route.axd?method=vast.sync.category.issued&format=Json"`，那么`@GET`的内容为 `@GET("/Route.axd?method=vast.sync.category.issued&format=Json")`。

###2、新建类RestClient

```
public class RestClient {

    private Retrofit mRetrofit;
    //上面例子URL的前部分
    private static final String BASE_URL = "http://plus.366ec.net";
    private RestService mService;

    public RestClient() {

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(RestService.class);
    }

    public RestService getRectService() {
        if (mService != null) {
            return mService;
        }
        return null;
    }

}
```

`RestClient` 类返回`RestService`接口的实例。

###3、新建类ApiResponse

```
public class ApiResponse<T> {

    @SerializedName("Code")
    public int code;

    @SerializedName("ServerTime")
    public String serverTime;

    @SerializedName("List")
    public List<T> categoryList;
}
```

我们提前来看一下返回的`Json`字符串，好分析一下解决`ApiResponse`怎么去写？

```
{"Code":"0","ServerTime":"2016/5/18 21:52:24","RowCount":"13","PageCount":"1","List":[{"categoryid":4,"parentid":0,"name":"书籍","status":1,"sortorder":0,"remark":"","productcount":"2"},{"categoryid":5,"parentid":0,"name":"历史","status":1,"sortorder":0,"remark":"","productcount":"0"},]}
```

你想一想，列表数据请求不同的地址可能返回回来的数据是不一样的，难道我每次都要去新建一个`ApiResponse`类吗，答案肯定是否的。用泛型就可以很好的解决这类问题。


###4、MainActivity类

一起看看怎么使用：

```
public class MainActivity extends AppCompatActivity {

    private RestClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new RestClient();

        Call<ApiResponse<Category>> data = mClient.getRectService().getCategory("1", "", "", "1", "20");

        data.enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                 Log.e("---------------",response.body().categoryList.get(0).name);
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {

            }
        });
    }
}

```

`getCategory("1", "", "", "1", "20");`接受参数，如果我想用`HashMap`来传递参数呢？

第一步修改`@GET`

```
    @GET("/Route.axd?method=vast.sync.category.issued&format=Json")
    Call<ApiResponse<Category>> getCategory(@QueryMap HashMap<String, String> hm);
```

注意：`@QueryMap`必须要用，不然会提示错误。`@GET`添加`@QueryMap`；`@POST`添加`@Body`。

第二步修改调用的地方：

```
        HashMap<String,String> hm=new HashMap<>();
        hm.put("StoreId","1");
        hm.put("Condition","");
        hm.put("LastUpdateTime","");
        hm.put("PageIndex","1");
        hm.put("PageSize","20");

        Call<ApiResponse<Category>> data = mClient.getRectService().getCategory(hm);

        data.enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                 Log.e("---------------",response.body().categoryList.get(0).name);
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {

            }
        });
```

当然后时候我们更希望获取返回的字符串，那我们又改怎么修改呢？

第一步：

```
    @GET("/Route.axd?method=vast.sync.category.issued&format=Json")
    Call<ResponseBody> getCategory(@QueryMap HashMap<String, String> hm);
```

第二步：

```
       HashMap<String,String> hm=new HashMap<>();
        hm.put("StoreId","1");
        hm.put("Condition","");
        hm.put("LastUpdateTime","");
        hm.put("PageIndex","1");
        hm.put("PageSize","20");

        Call<ResponseBody> data = mClient.getRectService().getCategory(hm);

        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ResponseBody body = response.body();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()));
                    StringBuilder out = new StringBuilder();
                    String newLine = System.getProperty("line.separator");//换行符号
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                        out.append(newLine);
                    }

                    // Prints the correct String representation of body.
                    System.out.println(out);
                    Log.e("---------------", ""+out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
```


##post请求(key/value)

```
    @FormUrlEncoded
    @POST("/Route.axd?method=vast.sync.category.issued&format=Json")
    Call<ApiResponse<Category>> postCategory(@Field("StoreId") String storeId, @Field("Condition") String condition,
                                             @Field("LastUpdateTime") String lastUpdateTime, @Field("PageIndex") String pageIndex,
                                             @Field("PageSize") String pageSize);
```

```
        Call<ApiResponse<Category>> data = mClient.getRectService().postCategory("1","","","1","20");

        data.enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {

                Log.e("----------",""+response.body().categoryList.get(0).name);
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {

            }
        });
```

##Post请求(body体)

```
    @POST("/Route.axd?method=vast.Store.terminal.bind&format=Json")
    Call<ApiResponse<Category>> postCategory(@Body Store store);
```

```
        Store store=new Store();

        store.StoreId="1";
        store.Condition="";
        store.LastUpdateTime="";
        store.PageIndex="1";
        store.PageSize="20";

        Call<ApiResponse<Category>> data = mClient.getRectService().postCategory("1","","","1","20");
```


注意：使用此方法会默认加上Content-Type: application/json; charset=UTF-8的请求头，即以JSON格式请求，再以JSON格  式响应。这里特别要注意的是JSON格式，类似`{"Condition":"","LastUpdateTime":"","PageIndex":"1","PageSize":"20","StoreId":"1"}`，如果&格式请求，就会出现错误，如`StoreId=1&Condition=&LastUpdateTime=&PageIndex=1&PageSize=20`。在使用的过程中你会发现`get`请求用`post`也可以请求，但使用此方法请区分开。

##单个文件上传

```
    @Multipart
    @POST("/HpWens/ProgressDemo/")
    Call<ResponseBody> uploadFile(Part("username") RequestBody username,@Part("address") RequestBody address,@Part  MultipartBody.Part file);
```

```
        mFileName = "6348.jpg";

        File file=new File(Environment.getExternalStorageDirectory(),mFileName);

        //普通key/value
        RequestBody username =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "ws");

        RequestBody address =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "天府之都");

        //file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        //包装RequestBody，在其内部实现上传进度监听
        CountingRequestBody countingRequestBody=new CountingRequestBody(requestFile, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
            }
        });

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), countingRequestBody);

        Call<ResponseBody> userCall=mClient.getRectService().uploadFile(username,address,body);
        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   Log.e("---------------","************"+response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("---------------","************1111"+t.getMessage());
            }
        });
```

##多文件上传

```
@Multipart
@POST("/HpWens/ProgressDemos/")
Call<ResponseBody> uploads(@PartMap Map<String, RequestBody> params);
```

```
/必须使用LinkedHashMap，保证文件按顺序上传
Map<String,RequestBody> params=new LinkedHashMap<>();
  File file1=new File(Environment.getExternalStorageDirectory(),"3436.jpg");
  RequestBody filebody1 =RequestBody.create(MediaType.parse("multipart/form-data"), file1);
  //记录文件上传进度
  CountingRequestBody countingRequestBody1=new CountingRequestBody(filebody1, new CountingRequestBody.Listener() {
      @Override
      public void onRequestProgress(long bytesWritten, long contentLength) {
          Log.e(TAG,"file1:"+contentLength+":"+bytesWritten);
      }
  });
  //file代表服务器接收到的key,file1.getName()代表文件名
  params.put("file\";filename=\""+file1.getName(),countingRequestBody1);


  File file2=new File(Environment.getExternalStorageDirectory(),"3435.jpg");
  RequestBody filebody2 =RequestBody.create(MediaType.parse("multipart/form-data"), file2);
  CountingRequestBody countingRequestBody2=new CountingRequestBody(filebody2, new CountingRequestBody.Listener() {
      @Override
      public void onRequestProgress(long bytesWritten, long contentLength) {
          Log.e(TAG,"file2:"+contentLength+":"+bytesWritten);
      }
  });
  params.put("file\";filename=\""+file2.getName(),countingRequestBody2);


  File file3=new File(Environment.getExternalStorageDirectory(),"3438.jpg");
  RequestBody filebody3 =RequestBody.create(MediaType.parse("multipart/form-data"), file3);
  CountingRequestBody countingRequestBody3=new CountingRequestBody(filebody3, new CountingRequestBody.Listener() {
      @Override
      public void onRequestProgress(long bytesWritten, long contentLength) {
          Log.e(TAG,"file3:"+contentLength+":"+bytesWritten);
      }
  });
  params.put("file\";filename=\""+file3.getName(),countingRequestBody3);

  //普通key/value
  params.put("username",   RequestBody.create(
          MediaType.parse("multipart/form-data"), "ws"));
  params.put("address", RequestBody.create(
          MediaType.parse("multipart/form-data"), "天府之都"));

  Call<ResponseBody> userCall=myService.uploads(params);
  userCall.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
           Log.e("-------", ""+response.body());

      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
         
      }
  });
```

##文件下载

```
    //文件下载
    @Streaming
    @GET("/33/photo/android-117d-85ed-4985-91f9-97684944{filename}")
    Call<ResponseBody> downFile(@Path("filename") String fileName);
```

注意：`filename`是访问地址截断的最后部分，如`http://jms-pic.b0.upaiyun.com/33/photo/android-117d-85ed-4985-91f9-976849446348.jpg`是图片地址`/33/photo/android-117d-85ed-4985-91f9-976849446348.jpg`的后部分，那么`{filename}`为`"6348.jpg"`

```
  Call<ResponseBody> userCall = mClient.getRectService().downFile(mFileName);

        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            
                try {
                    String fileName = Environment.getExternalStorageDirectory() + "/" + mFileName;

                    FileOutputStream fos = null;

                    fos = new FileOutputStream(fileName);
                    InputStream is = response.body().byteStream();

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    is.close();
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

```

图片就下载下来了，赶紧去我的文件中查看吧。

##开启OKHttp的日志拦截

`Retrofit2`底层还是使用的`OKHttp`,可以使用其相关的一些特性，比如开启日志拦截,此时就不能使用`Retrofit2`默认的`OKHttp`实例，开启日志后，会记录request和response的相关信息，需要自己单独构造，完整代码如下:

```
 HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
  
    public RestClient() {

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        mService = mRetrofit.create(RestService.class);
    }
```

`logcat`日志：

```
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: <-- 200 OK http://plus.366ec.net/Route.axd?method=vast.sync.category.issued&format=Json (207ms)
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Cache-Control: private
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Content-Length: 1356
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Content-Type: application/json; charset=utf-8
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Set-Cookie: ASP.NET_SessionId=cp5pfhe4mohzvopayfqpi3vs; path=/; HttpOnly
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Server: IIS
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: X-AspNet-Version: 0
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: X-Powered-By: WAF/2.0
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: Date: Wed, 18 May 2016 16:00:19 GMT
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: OkHttp-Sent-Millis: 1463587227552
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: OkHttp-Received-Millis: 1463587227614
05-19 00:00:27.610 16229-16287/com.github.ws.retrofitview D/OkHttp: {"Code":"0","Result":[],"ServerTime":"2016/5/19 
```

##Retrofit2与RxJava整合

`gradle`构建

```
    compile 'io.reactivex:rxandroid:1.1.0'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.0'
```

构造http接口类：

```
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加这一行
                .client(httpClient.build())
                .build();
```

这里举个简单的例子：

```
   @GET("/Route.axd?method=vast.sync.category.issued&format=Json")
    Observable<ApiResponse<Category>> getCategory(@QueryMap HashMap<String, String> hm);
```

```
        HashMap<String,String> hm=new HashMap<>();
        hm.put("StoreId","1");
        hm.put("Condition","");
        hm.put("LastUpdateTime","");
        hm.put("PageIndex","1");
        hm.put("PageSize","20");

       mClient.getRectService().getCategory(hm).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<ApiResponse<Category>>() {
                   @Override
                   public void onCompleted() {
                       
                   }

                   @Override
                   public void onError(Throwable e) {

                   }

                   @Override
                   public void onNext(ApiResponse<Category> categoryApiResponse) {

                   }
               });
```

`RxJava`支持链式写法，可以处理一些很复杂的问题。这里列出了`Get`的使用方法，其他的类似。

熬不住了，就写到这里了。最后源码需要的留言哈。
