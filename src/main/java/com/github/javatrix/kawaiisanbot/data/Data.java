package com.github.javatrix.kawaiisanbot.data;

import com.github.javatrix.kawaiisanbot.KawaiiSan;

import java.io.*;

public abstract class Data implements Serializable {

    private final File location;

    public Data(String location) {
        this.location = new File(KawaiiSan.DATA_DIRECTORY, location);
    }

    public void save() {
        try {
            if (!location.mkdirs()) {
                throw new IOException("Could not create parent directories.");
            }
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(location.getAbsoluteFile()));
            os.writeObject(this);
            os.close();
        } catch (IOException ex) {
            KawaiiSan.getInstance().getLogger().exception(ex);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "location=" + location +
                '}';
    }
}
