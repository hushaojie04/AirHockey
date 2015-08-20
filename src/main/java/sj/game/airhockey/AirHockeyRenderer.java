package sj.game.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2015/8/9.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AirHockeyRenderer";
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private Context context;
    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private int texture;
    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;
    private Geometry.Point previousBlueMalletPosition;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
//        final float aspectRatio = width  >height ? (float)width/height:(float)height/width;
//        if(width > height)
//        {
//            Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
//        }
//        else
//        {
//            Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
//        }
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
//        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        final float[] temp = new float[16];
//        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound + puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            Log.d("log",puckVector.x+" "+puckVector.y+" "+puckVector.z);
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            Log.d("log",puckVector.x+" "+puckVector.y+" "+puckVector.z);

            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Geometry.Point(clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y, clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));

        puckPosition = puckPosition.translate(puckVector);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

//        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();

        puckVector = puckVector.scale(0.99f);
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray = covertNormalized2DPointToRay(normalizedX, normalizedY);
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z),
                mallet.height / 2f);
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (malletPressed) {
            Geometry.Ray ray = covertNormalized2DPointToRay(normalizedX, normalizedY);
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f, clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius));

            float distance = Geometry.vectorBetWeen(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetWeen(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private Geometry.Ray covertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);
        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Geometry.Ray(nearPointRay, Geometry.vectorBetWeen(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}
