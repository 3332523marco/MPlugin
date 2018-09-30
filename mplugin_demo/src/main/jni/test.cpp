# include <jni.h>
# include <stdio.h>


extern "C"
{
    JNIEXPORT jstring JNICALL Java_mplugindemo_shengyuan_com_mplugin_1demo_MainActivity_getFromJNI(JNIEnv *env, jobject obj ){
    return env -> NewStringUTF("Hello i am from JNI!");
	}
}
