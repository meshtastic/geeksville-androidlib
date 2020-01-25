package com.geeksville.util

/**
 * This is a nasty hack to allow setting environment variables for unit tests
 * https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
 */
fun setEnv(k: String, v: String) {
    @Suppress("UNCHECKED_CAST", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")

    try {
        val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
        val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
        theEnvironmentField.isAccessible = true
        val env = theEnvironmentField.get(null) as java.util.Map<String, String>
        env.put(k, v)
        val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
        theCaseInsensitiveEnvironmentField.isAccessible = true
        val cienv = theCaseInsensitiveEnvironmentField.get(null) as java.util.Map<String, String>
        cienv.put(k, v)
    } catch (e: NoSuchFieldException) {
        val classes = java.util.Collections::class.java.declaredClasses
        val env = System.getenv()
        for (cl in classes) {
            if ("java.util.Collections\$UnmodifiableMap" == cl.name) {
                val field = cl.getDeclaredField("m")
                field.isAccessible = true
                val obj = field.get(env)
                val map = obj as java.util.Map<String, String>
                map.clear()
                map.put(k, v)
            }
        }
    }
}