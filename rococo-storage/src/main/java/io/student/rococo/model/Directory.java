package io.student.rococo.model;

public enum Directory {
    ARTISTS("artists"),
    MUSEUMS("museums"),
    PAINTINGS("paintings"),
    AVATARS("avatars");

    private final String folder;

    Directory(String folder) {
        this.folder = folder;
    }

    public String folder() {
        return folder;
    }
}