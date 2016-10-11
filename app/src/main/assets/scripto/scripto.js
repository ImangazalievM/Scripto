function Scripto() {}

 //вызывает Java метод и передает в него имя функции и аргументы
Scripto.call = function(interfaceName, args) {
    window[interfaceName].call(Scripto.getFunctionName(), Scripto.argsToJson(args));
}

 //вызывает Java метод и передает в него имя функции и аргументы
Scripto.callWithCallback = function(interfaceName, args) {
    if (args.length == 0) {
        throw "Function must have at least one argument!"
    }
    var callback = args[args.length - 1];
    if (typeof callback !== "function") {
        throw "Last argument must be a callback function!"
    }

    var correctsArguments = Array.prototype.slice.call(args, 0, args.length - 1); //удаляем коллбек из аргументов

    //получаем id коллбека, по которому мы будем его удалять после вызовва
    var callbackId = Scripto.addCallBack(callback);
    window[interfaceName].callWithCallback(Scripto.getFunctionName(), Scripto.argsToJson(correctsArguments), callbackId);
}

/* Вызов Java метода */
Scripto.argsToJson = function(args) {
    var argsArray = [];
    for(var i in args) {
        var item = args[i];
        argsArray.push(item);
    }
    var jsonArguments = JSON.stringify(argsArray);
    return jsonArguments;
}

/* Получает имя функции из стектрейса */
Scripto.getFunctionName = function() {
    var functionLine = (new Error()).stack.split('\n')[3].split(' ')[5];
    var functionCallStack = functionLine.split('.');
    var functionName = functionCallStack[functionCallStack.length - 1];
    return functionName;
};

/*--------------------------------------------------*/

/* Коллбеки */

Scripto.scriptoCallbacks = {};

Scripto.addCallBack = function(callback) {
    var callbackId = Scripto.generateCallbackId();
    Scripto.scriptoCallbacks[callbackId] = callback;
    return callbackId;
}

Scripto.removeCallBack = function(callbackId, response) {
    var callback =  Scripto.scriptoCallbacks[callbackId];
    callback(response);
    delete Scripto.scriptoCallbacks[callbackId];
}

Scripto.generateCallbackId = function() {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < 5; i++ ){
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
}

/*--------------------------------------------------*/

//если до этого были подключены скрипты работающие со Scripto,
//они оповещаются о готовности
var event = new CustomEvent("ScriptoPrepared");
document.dispatchEvent(event);