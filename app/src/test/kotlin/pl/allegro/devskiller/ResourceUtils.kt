package pl.allegro.devskiller

import java.io.File
import java.lang.IllegalArgumentException

class ResourceUtils {
    companion object {
        private val classLoader = ResourceUtils::class.java.classLoader
        fun getResourceString(resourceName: String) = File(
                classLoader.getResource(resourceName)?.file
                    ?: throw IllegalArgumentException("Cannot find $resourceName file")
            ).readText()
    }
}
