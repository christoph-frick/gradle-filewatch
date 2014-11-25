package net.ofnir.gradle.filewatch

class FileWatchConfig {

    final String name

    String path

    List<String> paths

    FileWatchConfig(String name) {
        this.name = name
    }

}
