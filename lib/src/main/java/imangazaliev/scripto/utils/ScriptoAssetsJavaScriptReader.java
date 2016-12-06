package imangazaliev.scripto.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Класс-хелпер для чтнения текстовых файлов
 */
public class ScriptoAssetsJavaScriptReader {

    private Context mContext;

    public ScriptoAssetsJavaScriptReader(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context не может быть null");
        }

        this.mContext = context;
    }

    /**
     * Читает текстовый файл из assets
     *
     * @return Содержимое текстового файла
     */
    public String read(String filePath) throws IllegalArgumentException {
        if (filePath == null) {
            throw new IllegalArgumentException("Имя файла не может быть null");
        }

        InputStream textFileStream = getTextFileInputStream(filePath);
        return textFileStream == null ? null : readTextFromInputStream(textFileStream);
    }

    private InputStream getTextFileInputStream(String filePath) {
        try {
            return mContext.getAssets().open(filePath);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Читает текст из InputStream
     *
     * @return Содержимое InputStream
     */
    private String readTextFromInputStream(InputStream textFileStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(textFileStream));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();

            String text = sb.toString();
            //убираем в конце последний символ, т. к. в цикле в конце добавился лишний символ \n
            text = text.substring(0, text.length() - 1);
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении InputStream", e);
        }
    }

}