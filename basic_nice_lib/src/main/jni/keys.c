#include <jni.h>

JNIEXPORT jstring JNICALL
JAVA_com_example_basic_nice_lib_getconvAPI(JNIEnv *env, jclass type){
    return (*env) -> NewStringUTF(env, "aHR0cHM6Ly9xYTE1YXBwLjNncWEuc2F0bWV0cml4LmNvbS9ucHhhcGkvY29udmVyc2F0aW9uL3YxLjAvaW5pdGlhdGU/c2VsZWN0ZWRMYW5ndWFnZT1lbl9VUw==");
}

JNIEXPORT jstring JNICALL
JAVA_com_example_basic_nice_lib_getreplyAPI(JNIEnv *env, jclass type){
    return (*env) -> NewStringUTF(env, "aHR0cHM6Ly9xYTE1YXBwLjNncWEuc2F0bWV0cml4LmNvbS9ucHhhcGkvY29udmVyc2F0aW9uL3YxLjAvcmVwbHk=");
}