![Scripto: Elegant Java-Javascript bridge](art/Scripto.png)

<p align="center">
<a href="https://bintray.com/imangazaliev/maven/scripto/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/imangazaliev/maven/scripto/images/download.svg" /></a>
<a href="https://android-arsenal.com/details/1/3983"><img alt="Android Arsenal" src="https://img.shields.io/badge/Android%20Arsenal-Scripto-brightgreen.svg?style=flat" /></a>
</p>

Android bridge for sending messages between Java and JavaScript in WebView.

[Русская версия (Russian version)](README-RU.md)

## Features

- convenient call of JS-functions/Java-methods with parameters
- call with callbacks support
- error handling in JS execution
- ability to transfer custom data types
- switching code execution to a UI thread

## Setup

1. Provide the gradle dependency:

```gradle
compile 'com.github.imangazalievm:scripto:2.1.1'
```

2. Copy the `scripto.js` file from `sample-app\src\main\assets\scripto` into assets folder.

If you want to see usage examples of the library, see `sample-app` folder.

## Usage

Firstly, we need to initialize the library:

```java
WebView webView = ...;
Scripto scripto = new Scripto.Builder(webView).build();
scripto.addJsFileFromAssets("scripto.js");
```


### Calling JS-functions from Java

1. For example, we have file ```login.js``` with some functions:

```javascript
function setLogin(login) {
    document.getElementById('login_field').value = login;
}

function getLogin() {
    return document.getElementById('login_field').value;
}
```

2. To call a function we must create Java-interface with JS-functions description:

```java
public interface LoginScript {

    JavaScriptFunctionCall<Void> setLogin(String login);

    JavaScriptFunctionCall<String> getLogin();

}
```

Methods must return JavaScriptFunctionCall. In the parameters of JavaScriptFunctionCall we specify type of JS-function response. In our case the first function returns nothing (Void), and the second returns text (String).

3. Then we must link Java-interface and JS-file:

```java
scripto.addJsFileFromAssets("login.js");

LoginScript loginScript = scripto.create(LoginScript.class);
```

The scripts must be linked to the interfaces before the page is loaded.

4. We can't use our script, because we need to wait for full page load, and for this we need to set a listener:

```java
scripto.onPrepared(new ScriptoPrepareListener() {
    @Override
    public void onScriptoPrepared() {
         loginScript.setLogin("MySuperLogin").call();
    }
});
```

5. To obtain data from a function use the following syntax:

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

1. Calling Java-methods from JavaScript very similar to default JavaScriptInterface. Create Java-class, which will act as JS-inteface:

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
For correct work JS-interface must not contain methods with the same name. Otherwisе library will throws an exception. Also we don't need to set ```@JavaScriptInterface``` annotation.

2. Add interface to Scripto:

```java
scripto.addInterface("Android", new AndroidInterface(context));
```

3. To call ```showToastMessage``` method we need to create JS-function with the same name:

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

4. Just as in Java we can use callbacks:

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

### Java methods call protection

If you need to protect methods from an unauthorized call, then you can protect them with the ```@JavaScriptSecure``` annotation:

```java
ScriptoInterfaceConfig config = new ScriptoInterfaceConfig().enableAnnotationProtection(true);
scripto.addInterface("Android", new AndroidInterface(this), config);
```

Don't forget to set the annotation on the method:

```java
@JavaScriptSecure
public void showToastMessage(String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
}
```

## License

```
The MIT License

Copyright (c) 2016-2018 Mahach Imangazaliev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
