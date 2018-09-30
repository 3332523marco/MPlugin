package mplugindemo.shengyuan.com.mplugin_core.hook;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;
import mplugindemo.shengyuan.com.mplugin_core.proxy.ProxyActivity;
import mplugindemo.shengyuan.com.mplugin_core.util.FileUtils;

/**
 * Created by mapeng on 2018/5/28.
 */

public class HookUtil {

    /**
     * 在Application中调用hook方法
     */

    private static final String TAG  = HookUtil.class.getSimpleName();
    private Context mContext;
    private static final String REAL_INTENT  = "realIntent";

    public HookUtil(Context context){
        mContext = context;
    }

    /********
     *
     * 注意：，可是在使用AppCompatActivity时，它又去向PackageManger去检测父类Activity，没找到。
     * 那怎么办，需要继续hook！
     * 这里为了简单起见，就采取Activity，只hook一次
     */
    public void hookIActivityManager() throws Exception {
        Log.i(TAG, "start hookIActivityManager");
        Class<?> activityManagerNativeClass;
        Field gDefaultFile;
        /**
         * 核心
         * 由于IActivityManagerSingleton是单例模式，可以拿到系统该单例对象并且修改该对象
         * 只有系统单例的对象修改才有效果
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1){
            //反射获取类
            activityManagerNativeClass = Class.forName("android.app.ActivityManager");
            //获取类中的字段
            gDefaultFile = activityManagerNativeClass.getDeclaredField("IActivityManagerSingleton");
        }else {
            //反射获取类
            activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            //获取类中的字段
            gDefaultFile = activityManagerNativeClass.getDeclaredField("gDefault");
        }
        //设置字段可访问
        gDefaultFile.setAccessible(true);
        //获取反射字段的值，静态方法，不需要传入对象，所以对象为null
        Object gDefaultFileValue = gDefaultFile.get(null);
        //获取gDefault.get()的值，主要在Singleton中
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceFile = singletonClass.getDeclaredField("mInstance");
        mInstanceFile.setAccessible(true);
        //非静态方法，需要传入对象,获取系统的IActivityManager
        Object IActivityManager = mInstanceFile.get(gDefaultFileValue);
        //获取IActivityManager接口

        Class<?> IActivityManagerClass = Class.forName("android.app.IActivityManager");
        //接下来需要创建钩子，替换系统的IActivityManager，主要采取动态代理的技术构造IActivityManager
        ProxyIActivityManager proxyIActivityManager= new ProxyIActivityManager(IActivityManager);
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IActivityManagerClass},
                proxyIActivityManager);
        //hook 就是为了替换IActivityManager的值,以下就是替换操作
        mInstanceFile.set(gDefaultFileValue, proxy);
        /////////到这里为止，已经实现了用代理Activity来替换未注册的Activity，通过PackageManagerService校验////////////
        //接下来找到系统的ActivityThread 并且要找到单例对象，才可以修改该对象值
    }

    public void hookActivityThreadHandler() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThreadValue = currentActivityThreadField.get(null);
        Field mHandlerField = activityThreadClass.getDeclaredField("mH");
        mHandlerField.setAccessible(true);
        Handler handlerValue = (Handler) mHandlerField.get(currentActivityThreadValue);
        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        mCallbackField.set(handlerValue, new HandlerCallback(handlerValue));
    }


    class HandlerCallback implements Handler.Callback{

        private Handler mHandler;
        public HandlerCallback(Handler handlerValue){
            mHandler = handlerValue;
        }

        @Override
        public boolean handleMessage(Message msg) {
            //LAUNCH_ACTIVITY 的what值是100
            if (msg.what == 100){
                //先处理自己的Handler消息，再处理ActivityThread中自身的handler消息
                try {
                    Log.i(TAG,"LAUNCH_ACTIVITY");
                    Object activityClientRecord = msg.obj;//ActivityClientRecord
                    Field intentField = activityClientRecord.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent proxyIntent = (Intent) intentField.get(activityClientRecord);
                    Intent realIntent = (Intent) proxyIntent.getParcelableExtra(REAL_INTENT);
                    if (realIntent != null){
                        //方法一，直接替换intent
                        //intentField.set(activityClientRecord, realIntent);
                        //方法二 替换component
                        proxyIntent.setComponent(realIntent.getComponent());
                    }

                }catch (Exception e){

                }
            }
            //处理ActivityThread中自身的handler消息
            mHandler.handleMessage(msg);
            return true;
        }
    }

    class ProxyIActivityManager implements InvocationHandler{

        private Object iActivityManager;

        public ProxyIActivityManager(Object iActivityManager){
            this.iActivityManager = iActivityManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i(TAG, "ProxyIActivityManager invoke:" + method.getName());
            if (method.getName().contains("startActivity")){

                int index = 0;
                Intent realIntent = null;
                for (int i = 0; i<args.length; i++){
                    if (args[i] instanceof Intent){
                        realIntent = (Intent) args[i];//真正的Intent，无法通过PackageManagerService检查
                        index = i;
                        break;
                    }
                }
                //代理Intent,可以通过PackageManagerService检查
                Intent proxyIntent = new Intent(mContext, ProxyActivity.class);
                proxyIntent.putExtra(REAL_INTENT, realIntent);
                args[index] = proxyIntent;
            }
            return method.invoke(iActivityManager, args);
        }
    }


    ///////////hook Instrumentation////////////////////
    /**
     * 问题的关键是怎么找到系统的Instrumentation
     * 之前说过ActivityThread是一个单例模式，其中就包含了Instrumentation
     * 所以只需要要替换掉ActivityThread中的Instrumentation即可
     *
     *只能在
     *
     *
     */

    public static void hookInstrumentation(Context context){
        try {
            Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
            //mMainThread就是一个ActivityThread
            Field activityThreadField = contextImplClass.getDeclaredField("mMainThread");
            activityThreadField.setAccessible(true);
            Object activityThreadValue = activityThreadField.get(context);
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentationValue = (Instrumentation) mInstrumentationField.get(activityThreadValue);

            //替换系统的mInstrumentation
            HookInstrumentationProxy instrumentationProxy = new HookInstrumentationProxy(context,
                    context.getPackageManager(),
                    mInstrumentationValue);
            mInstrumentationField.set(activityThreadValue,instrumentationProxy);
        }catch (Exception e){
            Log.i(TAG, "err:" + e.getMessage());
        }
    }

    public static ClassLoader hookApkInfo(Context context,String apkPath,boolean isCopySo){
        if(isCopySo) {
            String path = FileUtils.copySo(context, apkPath);
            insertNativeLibraryPathElements(new File(path),context);
        }
        HookUtil.hookInstrumentation(context);
        return hookDexElements(context,apkPath);
    }

    public static ClassLoader hookDexElements(Context context,String dexPath){
        try {
            PathClassLoader pathClassLoader = (PathClassLoader)context.getClassLoader();
            Class<?> herosClass = pathClassLoader.getClass().getSuperclass();
            Method m1 = herosClass.getMethod("addDexPath", String.class);
            m1.setAccessible(true);
            m1.invoke(pathClassLoader, dexPath);
            return pathClassLoader;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertNativeLibraryPathElements(File soDirFile, Context context){
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        if(pathList != null) {
            Field nativeLibraryPathElementsField = null;
            try {

                Method makePathElements;
                Object invokeMakePathElements;
                boolean isNewVersion = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1;
                //调用makePathElements
                makePathElements = isNewVersion?pathList.getClass().getDeclaredMethod("makePathElements", List.class):pathList.getClass().getDeclaredMethod("makePathElements", List.class,List.class,ClassLoader.class);
                makePathElements.setAccessible(true);
                ArrayList<IOException> suppressedExceptions = new ArrayList<>();
                List<File> nativeLibraryDirectories = new ArrayList<>();
                nativeLibraryDirectories.add(soDirFile);
                List<File> allNativeLibraryDirectories = new ArrayList<>(nativeLibraryDirectories);
                //获取systemNativeLibraryDirectories
                Field systemNativeLibraryDirectoriesField = pathList.getClass().getDeclaredField("systemNativeLibraryDirectories");
                systemNativeLibraryDirectoriesField.setAccessible(true);
                List<File> systemNativeLibraryDirectories = (List<File>) systemNativeLibraryDirectoriesField.get(pathList);
                Log.i("insertNativeLibrary","systemNativeLibraryDirectories "+systemNativeLibraryDirectories);
                allNativeLibraryDirectories.addAll(systemNativeLibraryDirectories);
                invokeMakePathElements = isNewVersion?makePathElements.invoke(pathClassLoader, allNativeLibraryDirectories):makePathElements.invoke(pathClassLoader, allNativeLibraryDirectories,suppressedExceptions,pathClassLoader);
                Log.i("insertNativeLibrary","makePathElements "+invokeMakePathElements);

                nativeLibraryPathElementsField = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
                nativeLibraryPathElementsField.setAccessible(true);
                Object list = nativeLibraryPathElementsField.get(pathList);
                Log.i("insertNativeLibrary","nativeLibraryPathElements "+list);
                Object dexElementsValue = combineArray(list, invokeMakePathElements);
                //把组合后的nativeLibraryPathElements设置到系统中
                nativeLibraryPathElementsField.set(pathList,dexElementsValue);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public static Object combineArray(Object hostDexElementValue, Object pluginDexElementValue) {
        //获取原数组类型
        Class<?> localClass = hostDexElementValue.getClass().getComponentType();
        Log.i("insertNativeLibrary","localClass "+localClass);
        //获取原数组长度
        int i = Array.getLength(hostDexElementValue);
        //插件数组加上原数组的长度
        int j = i + Array.getLength(pluginDexElementValue);
        //创建一个新的数组用来存储
        Object result = Array.newInstance(localClass, j);
        //一个个的将dex文件设置到新数组中
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(hostDexElementValue, k));
            } else {
                Array.set(result, k, Array.get(pluginDexElementValue, k - i));
            }
        }
        return result;
    }

    public static Object getPathList(Object classLoader) {
        Class cls = null;
        String pathListName = "pathList";
        try {
            cls = Class.forName("dalvik.system.BaseDexClassLoader");
            Field declaredField = cls.getDeclaredField(pathListName);
            declaredField.setAccessible(true);
            return declaredField.get(classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
