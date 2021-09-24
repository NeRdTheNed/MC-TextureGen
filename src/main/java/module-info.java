// Currently never compiled due to MC-TextureGen targeting Java 1.4 TODO Find a better solution
module MCTextureGenerator {
    requires transitive java.desktop;
    requires transitive java.logging;

    exports mcTextureGen;
    exports mcTextureGen.data;
    exports mcTextureGen.generators;
}
