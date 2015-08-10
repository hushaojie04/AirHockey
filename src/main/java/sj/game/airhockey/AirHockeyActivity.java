package sj.game.airhockey;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by Administrator on 2015/8/9.
 */
public class AirHockeyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new AirHockeyRenderer(this));
        setContentView(glSurfaceView);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configuration = activityManager.getDeviceConfigurationInfo();
        LogUtil.L("reqGlEsVersion:"+configuration.reqGlEsVersion);
    }

}
