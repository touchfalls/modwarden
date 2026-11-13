stonecutter active "26.3"

stonecutter parameters {
    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
        // Fabric API 26.1 renamed the payload registries
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.playC2S()", "PayloadTypeRegistry.serverboundPlay()")
        }
    }
}
