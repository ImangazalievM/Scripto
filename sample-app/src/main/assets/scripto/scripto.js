var Scripto = new function() {

    var scriptoCallbacks = {};

    // Converts arguments to JSON
    var argsToJson = function(args) {
        var argsArray = [];
        for(var i in args) {
            var item = args[i];
            argsArray.push(item);
        }
        var jsonArguments = JSON.stringify(argsArray);
        return jsonArguments;
    }

    // Extracts function name from stacktrace
    var getFunctionName = function() {
        var functionLine = (new Error()).stack.split('\n')[3].split(' ')[5];
        var functionCallStack = functionLine.split('.');
        var functionName = functionCallStack[functionCallStack.length - 1];
        return functionName;
    };

    var addCallBack = function(callback) {
        var callbackId = generateCallbackId();
        scriptoCallbacks[callbackId] = callback;
        return callbackId;
    }

    var generateCallbackId = function() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for( var i=0; i < 5; i++ ){
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }
        return text;
    }

    // Calls Java method and passes him function name with arguments
    this.call = function(interfaceName, args) {
        window[interfaceName].call(getFunctionName(), argsToJson(args));
    };

    // Calls Java method and passes him function name with arguments and callback identifier
    this.callWithCallback = function(interfaceName, args) {
        if (args.length == 0) {
            throw "Function must have at least one argument!"
        }
        var callback = args[args.length - 1];
        if (typeof callback !== "function") {
            throw "Last argument must be a callback function!"
        }

        //remove callback from arguments
        var correctsArguments = Array.prototype.slice.call(args, 0, args.length - 1);

        //callbackId used to delete the callback after its call
        var callbackId = addCallBack(callback);
        window[interfaceName].callWithCallback(getFunctionName(), argsToJson(correctsArguments), callbackId);
    };

    this.removeCallBack = function(callbackId, response) {
        var callback =  scriptoCallbacks[callbackId];
        callback(response);
        delete scriptoCallbacks[callbackId];
    }

}

// Custom Event support for old devices
if (!window.CustomEvent) {
  window.CustomEvent = function (type) {
    var e = document.createEvent('CustomEvent');
    e.initCustomEvent(type);
    return e;
  };
}

// If we have scripts connected earlier, they are notified of the readiness of Scripto
var event = new CustomEvent("ScriptoPrepared");
document.dispatchEvent(event);