// Currently never compiled due to MC-TextureGen targeting Java 1.4 TODO Find a better solution
module com.github.nerdthened.mctexturegen {
    requires transitive java.desktop;
    requires transitive java.logging;

    exports com.github.nerdthened.mctexturegen;
    exports com.github.nerdthened.mctexturegen.data;
    exports com.github.nerdthened.mctexturegen.generators;
}
