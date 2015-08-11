package sj.game.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2015/8/9.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AirHockeyRenderer";

    private final float[] projectMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private Context context;
    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private int texture;
    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
//        final float aspectRatio = width  >height ? (float)width/height:(float)height/width;
//        if(width > height)
//        {
//            Matrix.orthoM(projectMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
//        }
//        else
//        {
//            Matrix.orthoM(projectMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
//        }
        MatrixHelper.perspectiveM(projectMatrix, 45, (float) width / (float) height, 1f, 0f);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectMatrix,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }
}
