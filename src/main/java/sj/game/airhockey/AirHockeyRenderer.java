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
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * 4;
    FloatBuffer vertexData;
    final String vertexShaderSource;
    final String fragmentShaderSource;
    int mProgram = 0;
    private final float[] projectMatrix = new float[16];
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX="u_Matrix";
    private static final String A_POSITION = "a_Position";
    private int aColorHandle = -1;
    private int aPositionHandle = -1;
    private int uMatrixHandle = -1;
    public AirHockeyRenderer(Context context) {
        vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        float[] tableVerticesWithTriangles = {
                0f,     0f,        1f, 1f, 1f,
                - 0.5f, -0.9f,     0.7f, 0.7f, 0.7f,
                0.5f, -0.9f,       0.7f, 0.7f, 0.7f,
                0.5f, 0.9f,        0.7f, 0.7f, 0.7f,
                -0.5f, 0.9f,       0.7f, 0.7f, 0.7f,
                -0.5f, -0.9f,      0.7f, 0.7f, 0.7f,


                //line
                -0.5f, 0f,         1f, 0f, 0f,
                0.5f, 0f,          1f, 0f, 0f,
                //mallets
                0f, -0.25f,        0f, 0f, 1f,
                0f, 0.25f,         1f, 0f, 0f
        };

        vertexData = BufferUtils.getFloatBuffer(tableVerticesWithTriangles);


        mProgram = ShaderHelper.createProgram(vertexShaderSource, fragmentShaderSource);
        LogUtil.L(TAG, "mProgram=" + mProgram);
        GLES20.glUseProgram(mProgram);
        aPositionHandle = GLES20.glGetAttribLocation(mProgram, A_POSITION);
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionHandle);

        aColorHandle = GLES20.glGetAttribLocation(mProgram, A_COLOR);
        vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorHandle);

        uMatrixHandle = GLES20.glGetUniformLocation(mProgram,U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        final float aspectRatio = width  >height ? (float)width/height:(float)height/width;
        if(width > height)
        {
            Matrix.orthoM(projectMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
        }
        else
        {
            Matrix.orthoM(projectMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectMatrix,0);
//        GLES20.glUniform4f(uColorHandle, 1.0f, 1f, 1f, 1f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
        //line
//        GLES20.glUniform4f(uColorHandle, 1.0f, 0f, 1f, 1f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        //point
//        GLES20.glUniform4f(uColorHandle, 0.0f, 0f, 1f, 1f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
//        GLES20.glUniform4f(uColorHandle, 1.0f, 0f, 1f, 1f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

    }
}
