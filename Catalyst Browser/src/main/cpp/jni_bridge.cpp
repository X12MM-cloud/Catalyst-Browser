#include <jni.h>
#include <string>
#include "cef_wrapper.h"
#include <include/base/cef_logging.h>

// JNI method signatures
extern "C" {

// Initialize CEF and create the browser
JNIEXPORT jboolean JNICALL Java_com_zetax_zetabrowser_NativeBridge_initialize
  (JNIEnv* env, jclass clazz) {
    return CefWrapper::GetInstance()->Initialize();
}

// Shutdown CEF
JNIEXPORT void JNICALL Java_com_zetax_zetabrowser_NativeBridge_shutdown
  (JNIEnv* env, jclass clazz) {
    CefWrapper::GetInstance()->Shutdown();
}

// Create a browser with the specified URL
JNIEXPORT jboolean JNICALL Java_com_zetax_zetabrowser_NativeBridge_createBrowser
  (JNIEnv* env, jclass clazz, jstring url) {
    const char* nativeUrl = env->GetStringUTFChars(url, nullptr);
    bool result = CefWrapper::GetInstance()->CreateBrowser(nativeUrl);
    env->ReleaseStringUTFChars(url, nativeUrl);
    return result;
}

// Load a URL in the browser
JNIEXPORT void JNICALL Java_com_zetax_zetabrowser_NativeBridge_loadURL
  (JNIEnv* env, jclass clazz, jstring url) {
    const char* nativeUrl = env->GetStringUTFChars(url, nullptr);
    CefWrapper::GetInstance()->LoadURL(nativeUrl);
    env->ReleaseStringUTFChars(url, nativeUrl);
}

// Execute JavaScript in the browser
JNIEXPORT void JNICALL Java_com_zetax_zetabrowser_NativeBridge_executeJavaScript
  (JNIEnv* env, jclass clazz, jstring code) {
    const char* nativeCode = env->GetStringUTFChars(code, nullptr);
    CefWrapper::GetInstance()->ExecuteJavaScript(nativeCode);
    env->ReleaseStringUTFChars(code, nativeCode);
}

// Get the latest rendered frame buffer
JNIEXPORT jbyteArray JNICALL Java_com_zetax_zetabrowser_NativeBridge_getLatestBuffer
  (JNIEnv* env, jclass clazz, jobject dimensions) {
    std::vector<uint8_t> buffer;
    int width = 0;
    int height = 0;
    
    if (!CefWrapper::GetInstance()->GetLatestBuffer(buffer, width, height)) {
        return nullptr;
    }
    
    // Get the class and field IDs for the Dimension object
    jclass dimensionClass = env->GetObjectClass(dimensions);
    jfieldID widthField = env->GetFieldID(dimensionClass, "width", "I");
    jfieldID heightField = env->GetFieldID(dimensionClass, "height", "I");
    
    // Set the width and height in the Dimension object
    env->SetIntField(dimensions, widthField, width);
    env->SetIntField(dimensions, heightField, height);
    
    // Create a byte array to return the buffer
    jbyteArray result = env->NewByteArray(buffer.size());
    env->SetByteArrayRegion(result, 0, buffer.size(), reinterpret_cast<jbyte*>(buffer.data()));
    
    return result;
}

} // extern "C"