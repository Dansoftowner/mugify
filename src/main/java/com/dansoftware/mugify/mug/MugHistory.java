package com.dansoftware.mugify.mug;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MugHistory {
    private final List<MugEntry> entries = new ArrayList<>();
    private final List<MugEntry> active = new ArrayList<>();
    private final List<Runnable> callbacks = new ArrayList<>();

    public void loadPairs(List<MugEntry> pairs) {
        this.entries.addAll(pairs);
        notifyCallbacks();
    }

    public void addMug(MugEntry entry) {
        if (!entries.contains(entry)) {
            this.entries.add(entry);
            notifyCallbacks();
        }
    }

    public List<MugEntry> listPairs() {
        return Collections.unmodifiableList(entries);
    }

    public boolean deleteByName(String name) {
        boolean removed = entries.removeIf(entry -> Objects.equals(entry.name, name));
        active.removeIf(entry -> Objects.equals(entry.name, name));
        if (removed) notifyCallbacks();
        return removed;
    }

    public boolean deleteByFilePath(String filePath) {
        boolean removed = entries.removeIf(entry -> Objects.equals(entry.filePath, filePath));
        active.removeIf(entry -> Objects.equals(entry.filePath, filePath));
        if (removed) notifyCallbacks();
        return removed;
    }

    public boolean containsMugName(String name) {
        return entries.stream().anyMatch(entry -> Objects.equals(entry.name, name));
    }

    public void addActive(MugEntry entry) {
        if (entry != null && entries.contains(entry) && !active.contains(entry)) {
            this.active.add(entry);
            entry.stage.setOnCloseRequest(event -> active.remove(entry));
        }
    }

    public void removeActive(MugEntry entry) {
        this.active.remove(entry);
        entry.stage.setTitle("Mugify");
        entry.stage = null;
    }

    public List<MugEntry> getActive() {
        return Collections.unmodifiableList(active);
    }

    public boolean isActive(String mugName) {
        return active.stream().anyMatch(entry -> Objects.equals(entry.name, mugName));
    }

    public boolean isActiveFile(String filePath) {
        return active.stream().anyMatch(entry -> Objects.equals(entry.filePath, filePath));
    }

    public void clearActive() {
        this.active.clear();
    }

    public MugEntry findByName(String name) {
        return entries.stream()
                .filter(entry -> Objects.equals(entry.name, name))
                .findFirst()
                .orElse(null);
    }

    public MugEntry findByFilePath(String filePath) {
        return entries.stream()
                .filter(entry -> Objects.equals(entry.filePath, filePath))
                .findFirst()
                .orElse(null);
    }

    public Stage getStageByName(String name) {
        MugEntry entry = findByName(name);
        return entry != null ? entry.getStage() : null;
    }

    public Stage getStageByFilePath(String filePath) {
        MugEntry entry = findByFilePath(filePath);
        return entry != null ? entry.getStage() : null;
    }

    public void setStage(MugEntry entry, Stage stage) {
        if (entry != null && entries.contains(entry)) {
            clearStageFromOthers(stage, entry);
            entry.setStage(stage);
            stage.setTitle("Mugify - " + entry.getName() + " (" + entry.getFilePath() + ")");
            addActive(entry);
        }
    }

    public void disassociateStage(Stage stage) {
        clearStageFromOthers(stage, null);
    }

    private void clearStageFromOthers(Stage stage, MugEntry exceptEntry) {
        for (MugEntry entry : active) {
            if (entry != exceptEntry && Objects.equals(entry.getStage(), stage)) {
                entry.setStage(null);
                stage.setTitle("Mugify");
            }
        }
    }

    public void addCallback(Runnable callback) {
        callbacks.add(callback);
    }

    private void notifyCallbacks() {
        callbacks.forEach(Runnable::run);
    }

    public MugEntry getActiveEntryForStage(Stage stage) {
        return active.stream()
                .filter(entry -> entry.getStage() == stage)
                .findFirst()
                .orElse(null);
    }

    public void updateEntryName(MugEntry mugEntry, String name) {
        if (!entries.contains(mugEntry))
            return;
        mugEntry.name = name;
        if (mugEntry.stage != null) {
            mugEntry.stage.setTitle("Mugify - " + mugEntry.getName() + " (" + mugEntry.getFilePath() + ")");
        }
    }

    public static class MugEntry {
        private String name;
        private final String filePath;
        private Stage stage;

        public MugEntry(String name, String filePath) {
            this.name = name;
            this.filePath = filePath;
            this.stage = null;
        }

        public MugEntry(String name, String filePath, Stage stage) {
            this.name = name;
            this.filePath = filePath;
            this.stage = stage;
        }

        public String getName() {
            return name;
        }

        public String getFilePath() {
            return filePath;
        }

        public Stage getStage() {
            return stage;
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MugEntry mugEntry = (MugEntry) o;
            return Objects.equals(name, mugEntry.name) && Objects.equals(filePath, mugEntry.filePath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, filePath);
        }
    }
}