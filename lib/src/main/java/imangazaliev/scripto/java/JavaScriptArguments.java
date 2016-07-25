package imangazaliev.scripto.java;

import imangazaliev.scripto.Scripto;
import imangazaliev.scripto.converter.JavaScriptConverter;
import imangazaliev.scripto.utils.ScriptoUtils;

public class JavaScriptArguments {

    private Scripto scripto;
    private String[] stringArgs;

    public JavaScriptArguments(Scripto scripto, Object[] argsObjects) {
        this.scripto = scripto;
        this.stringArgs = initArgs(argsObjects);
    }

    public String[] initArgs(Object[] argsObjects) {
        //функция была вызвана без аргументов
        if (argsObjects == null) {
            return new String[0];
        }

        JavaScriptConverter javaScriptConverter = scripto.getJavaScriptConverter();
        String[] resultArgs = new String[argsObjects.length];

        for (int i = 0; i < argsObjects.length; i++) {
            Object argument = argsObjects[i];
            //конвертируем аргумент в строку
            if (argument == null) {
                resultArgs[i] = "null";
            } else if (ScriptoUtils.isPrimitiveWrapper(argument.getClass()) || argument.getClass().isPrimitive()) {
                //если это примитивные типы или обертки, просто конвертируем в строку
                resultArgs[i] = String.valueOf(argument);
            } else if (argument.getClass() == String.class) {
                //добавляем к строке кавычки для правильной передачи
                resultArgs[i] = String.format("'%s'", argument);
            } else {
                //если аргумент является объектом, конвертируем в json
                Object arg = argsObjects[i];
                resultArgs[i] = javaScriptConverter.convertToString(arg, arg.getClass());
            }
        }
        return resultArgs;
    }

    public String getArguments() {
        String resultArgsString = "";
        for (int i = 0; i < stringArgs.length; i++) {
            //если аргумент не первый, добавляем запятую
            resultArgsString += (i == 0) ? stringArgs[i] : "," + stringArgs[i];
        }
        return resultArgsString;
    }

    public int getArgumentsCount() {
        return stringArgs.length;
    }

}
