package io.github.soju06.filesystems.watch;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class WatchManager<TDaemon extends WatchDaemon> implements Closeable, Iterable<TDaemon> {
    private final List<TDaemon> Daemons = new ArrayList<>();

    public int count() {
        return Daemons.size();
    }

    public int watchAll() {
        var i = 0;
        for (TDaemon daemon : this) {
            try {
                daemon.watch();
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public boolean add(TDaemon daemon) {
        if (Daemons.contains(daemon)) return false;
        Daemons.add(daemon);
        return true;
    }

    public boolean remove(TDaemon daemon) {
        return Daemons.remove(daemon);
    }

    public boolean contains(TDaemon daemon) {
        return Daemons.contains(daemon);
    }

    public boolean contains(String path) {
        return Daemons.stream().anyMatch(daemon -> daemon.getPath().equals(path));
    }

    public Stream<TDaemon> stream() {
        return Daemons.stream();
    }

    public TDaemon get(int index) {
        return Daemons.get(index);
    }

    public TDaemon get(String path) {
        for (TDaemon daemon : Daemons) {
            if (daemon.getPath().equals(path))
                return daemon;
        }
        return null;
    }

    public List<TDaemon> toList() {
        return Daemons.stream().toList();
    }

    @Override
    public void close() throws IOException {
        for (WatchDaemon daemon : Daemons) {
            if (daemon != null)
                daemon.close();
        }
        Daemons.clear();
    }

    @NotNull
    @Override
    public Iterator<TDaemon> iterator() {
        return Daemons.iterator();
    }
}
