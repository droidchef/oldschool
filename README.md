OldSchool

The app uses MVVM Architecture by leveraging the Android provided ViewModel and LiveData classes.

I've not used any Third Party library to implement the feature set.

For Networking I've created a simple `NetworkRequestManager` that runs an `ExecutorService` to
process `NetworkRequests`. The Asynchronous communication happens via `Callbacks`.

In production I'd *almost always avoid* them by making use of other things like RxJava and/or Kotlin Coroutines.

For parsing Json, I've written my own `JsonToEntityConverter` which registers all the types that
we want to parse at one place and also the strategy for parsing them.

There's also a Unit Test for testing this class called `JsonToEntityConverterTest`. This is a RobolectricTest
because JSONObject is a part of the Android SDK.

For loading images, I've created an `ImageLoadingManager` that exposes a simple Picasso like API not
completely mimicking the builder pattern but that was an inspiration. Under the hood it also uses
an `ExecutorService` to process requests on a background thread to download the `Bitmap` data and
then posts it into an `ImageView`.

To spruce things up, I've also added a simple `LruCache` for the Caching Images and Response payloads
for queries in Memory. I have not implemented the LruCache myself but leveraged the one offer by the
Android SDK itself.

For Interacting with the Data I make use of a a Repository that looks up the cache and then eventually
hits the network if the cache doesn't contain the data.

I've not use any DI here as expected but tried to keep my dependencies a bit decoupled so that I could
still unit test my repository.

The API key for the FlickrAPI is hardcoded in the network request URL for now. This is an anti-pattern
and I won't do it in production.

The ideal way for me to do it would be to inject the API key via BuildConfig at compile time by reading it
through a secured storage in the build environment like Vault or Kubernetes Secret. OR By scrambling and hiding
it in a packaged SO library that exposes it to our app.

Also, I'd use OkHttp for networking which would allow me to add an Interceptor that I'd use to put the
API key into the requests instead of sending it along with the URL query params every single time.

For Image Loading also I'd prefer an already stable and regularly maintained library like Glide or Picasso.

