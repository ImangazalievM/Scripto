![Scripto: Elegant Java-Javascript bridge](art/Scripto.png)

<p align="center">
<a href="https://bintray.com/imangazaliev/maven/scripto/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/imangazaliev/maven/scripto/images/download.svg" /></a>
<a href="https://android-arsenal.com/details/1/3983"><img alt="Android Arsenal" src="https://img.shields.io/badge/Android%20Arsenal-Scripto-brightgreen.svg?style=flat" /></a>
</p>

[Русская версия (Russian version)](README-RU.md)

Android bridge for sending messages between Java and JavaScript in WebView.

## Setup

```gradle
compile 'com.github.imangazalievm:scripto:2.1.1"
```

## Using the library

Firstly you must copy the ```scripto.js``` file from sample project into assets folder and initialize the library:

```java
WebView webView = ...;
Scripto scripto = new Scripto.Builder(webView).build();
scripto.addJsFileFromAssets("scripto.js");
```

### Calling JS-functions from Java

For example, we have file ```login.js``` with some functions:

```javascript
function setLogin(login) {
    document.getElementById('login_field').value = login;
}

function getLogin() {
    return document.getElementById('login_field').value;
}
```

To call a function we must create Java-interface with JS-functions description:

```java
public interface LoginScript {

    JavaScriptFunctionCall<Void> setLogin(String login);

    JavaScriptFunctionCall<String> getLogin();

}
```

Methods must return JavaScriptFunctionCall. In the parameters of JavaScriptFunctionCall we specify type of JS-function response. In our case the first function returns nothing (Void), and the second returns text (String).

Then we must link Java-interface and JS-file:
```java
scripto.addJsFileFromAssets("login.js");
LoginScript loginScript = scripto.create(LoginScript.class);
```

The scripts must be linked to the interfaces before the page is loaded.

We can't use our script, because we need to wait for full page load. For this we need to set a listener:

```java
scripto.onPrepared(new ScriptoPrepareListener() {
    @Override
    public void onScriptoPrepared() {
         loginScript.setLogin("MySuperLogin").call();
    }
});
```

To obtain data from a function use the following syntax:

```java
loginScript.getLogin()
    .onResponse(login -> Toast.makeText(MainActivity.this, login, Toast.LENGTH_LONG).show())
    .call();
```

Also we can handle errors, caused in JS-code:

```java
.onError(error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show())
```

For the conversion of user-defined data type from JSON used GSON library.

If you want to get raw JSON, then necessarily to use class RawReponse:

```java
JavaScriptFunctionCall<RawResponse> getJson();
```

Receiving JSON:

```java
loginScript.getJson()
    .onResponse(response -> Toast.makeText(MainActivity.this, response.getResponse(), Toast.LENGTH_LONG).show())
    .call();
```

### Calling Java-methods from JavaScript

Calling Java-methods from JavaScript very similar to default JavaScriptInterface. Create Java-class, which will act as JS-inteface:

```java
public class AndroidInterface {

    private Context context;

    public AndroidInterface(Context context) {
        this.context = context;
    }

    public void showToastMessage(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
```
For correct work of JS-interface must not contain methods with the same name. Otherwisе library will throws an exception. Also we don't need to set ```@JavaScriptInterface``` annotation.

Add interface:

```java
scripto.addInterface("Android", new AndroidInterface(context));
```

To call ```showToastMessage``` method we need to create JS-function with the same name:

```javascript
function showToastMessage(text) {
  Scripto.call('Android', arguments);
};
```

In the function we call special library function  and pass name of JS-interface  and arguments.

Calling the method from JavaScript:

```javascript
showToastMessage("My super message");
```

Just as in Java we can use callbacks:
```java
public String showToastMessage(String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    return "My super response";
}
```

Calling the method from JavaScript:

```javascript
showToastMessage("My super message", function(responseString) {
    console.log(responseString);
});
```

If you want to pass  user-defined data type from JavaScript, convert your data to JSON via ```JSON.stringify(object)```.

If you need to protect methods from an unauthorized call, then you can protect them  ```@JavaScriptSecure``` annotation:

```java
ScriptoInterfaceConfig config = new ScriptoInterfaceConfig().enableAnnotationProtection(true);
scripto.addInterface("Android", new AndroidInterface(this), config);
```


Do not forget to set an annotation on the method:

```java
@JavaScriptSecure
public void showToastMessage(String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
}
```

## License

```
The MIT License

Copyright (c) 2016 Mahach Imangazaliev 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```