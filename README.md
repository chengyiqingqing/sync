# keepProcessLiving
android 双进程防杀死，+JobService保活

下面是一些adb命令帮助你验证进程保活；
以下命令可以帮助你通过查看该应用的进程名及进程数，进程优先级，
service是否为前台进程等等。


    adb devices                          查看当前设备。
    adb shell ps | grep <package name>   查看当前应用的所有进程。可以不打全名。√
    adb shell cat /proc/<pid>/oom_adj    查看当前进程的优先级。值越大，越容易被杀 √
    adb shell kill [pid]                 杀死进程id  
    adb shell dumpsys activity services [<packagename>]  查看正在运行的services  
    adb shell service list               查看后台services信息
    adb shell dumpsys notification       获取通知信息。
    
   应用管理类
   
    adb shell pm list packages -3        -3/qq  查看第三方应用/查看包名带有qq的应用。
    adb shell pm clear <packagename>     清除apk数据与缓存。
    
    
   当然，你也可以使使用Android devices monitor 和adb plugins 来查看和管理你的进程。
   
   追加：
   
   账号自动同步和手动同步参考文档。https://www.jianshu.com/p/dc9a2693478e
    

