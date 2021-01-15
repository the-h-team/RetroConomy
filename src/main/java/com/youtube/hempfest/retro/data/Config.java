package com.youtube.hempfest.retro.data;

import com.youtube.hempfest.retro.RetroConomy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private final String n;
    private final String d;
    private FileConfiguration fc;
    private File file;
    private static final List<Config> configs = new ArrayList<>();

    private Config(final String n, final String d) {
        this.n = n;
        this.d = d;
        configs.add(this);
    }

    public static void copy(InputStream in, File file) {
        CompletableFuture.runAsync(() -> {
            try {
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();
    }

    public String getName() {
        return (n == null) ? "" : n;
    }

    public String getDirectory() {
        return (d == null) ? "" : d;
    }

    public static Config get(final String n, final String d) {
        for (final Config c : Config.configs) {
            if (c.getName().equals(n) && c.getDirectory().equals(d)) {
                return c;
            }
        }
        if (n != null && !n.isEmpty()) {
            return new Config(n, d);
        }
        return null;
    }

    public boolean delete() {
        Config.configs.removeIf(c -> c.equals(this));
        return this.getFile().delete();
    }

    public boolean exists() {
        if (this.file == null) {
            final File temp = new File(this.getDataFolder(), this.getName() + ".yml");
            if (!temp.exists()) {
                return false;
            } else {
                this.file = temp;
            }
        }
        return true;
    }

    public File getFile() {
        if(this.file == null) {
            this.file = new File(this.getDataFolder(), this.getName() + ".yml"); //create method get data folder
            CompletableFuture.runAsync(() -> {
                if(!this.file.exists()) {
                    try {
                        this.file.createNewFile();
                    }catch(final IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return this.file;
    }

    public FileConfiguration getConfig() {
        if(this.fc == null) {
            CompletableFuture.runAsync(() -> this.fc = YamlConfiguration.loadConfiguration(this.getFile())).join();
        }
        return this.fc;
    }

    public File getDataFolder() {
        final File dir = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File d;
        if (this.d != null) {
            d = new File(dir.getParentFile().getPath(), RetroConomy.getInstance().getName() + "/" + this.d + "/");
        } else {
            d = new File(dir.getParentFile().getPath(), RetroConomy.getInstance().getName());
        }
        if (!d.exists()) {
            d.mkdirs();
        }
        return d;
    }

    public static Config getOptions() {
        return Config.get("Options", "Settings");
    }

    public static Config get(AccountFile type) {
        if (type == AccountFile.BANK) {
            return get("Accounts", "Bank");
        }
        if (type == AccountFile.SERVER) {
            return get("Accounts", "Server");
        }
        if (type == AccountFile.ENTITY) {
            return get("Accounts", "Entity");
        }
        return null;
    }

    public enum AccountFile {
        SERVER, ENTITY, BANK
    }

    public synchronized void reload() { // fork io but still wait for it on main thread; sync object access
        CompletableFuture.runAsync(() -> {
            this.file = new File(getDataFolder(), getName() + ".yml");
            if (!this.file.exists())
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            this.fc = YamlConfiguration.loadConfiguration(this.file);
            File defConfigStream = new File(getDataFolder(), getName() + ".yml");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.fc.setDefaults(defConfig);
            configs.removeIf(c -> c.getName().equals(n));
        }).join();
    }

    public synchronized void saveConfig() { // fork io but still wait for it on main thread; sync object access
        CompletableFuture.runAsync(() -> {
            try {
                this.getConfig().save(this.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).join();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;
        Config config = (Config) o;
        return n.equals(config.n) &&
                d.equals(config.d) &&
                Objects.equals(fc, config.fc) &&
                Objects.equals(getFile(), config.getFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, d);
    }
}

