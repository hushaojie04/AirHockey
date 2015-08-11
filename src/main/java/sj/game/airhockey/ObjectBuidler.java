package sj.game.airhockey;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/11.
 */
public class ObjectBuidler {
    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        public GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

    private int offset = 0;

    private ObjectBuidler(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadian = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = circle.center.x + circle.radius * FloatMath.cos(angleInRadian);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * FloatMath.sin(angleInRadian);
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * FloatMath.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * FloatMath.sin(angleInRadians);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;

            drawList.add(new DrawCommand() {
                @Override
                public void draw() {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
                }
            });
        }
    }

    public static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuidler buidler = new ObjectBuidler(size);
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius);

        buidler.appendCircle(puckTop, numPoints);
        buidler.appendOpenCylinder(puck, numPoints);
        return buidler.build();
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuidler buidler = new ObjectBuidler(size);
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
        buidler.appendCircle(baseCircle, numPoints);
        buidler.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3;
        Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);
        buidler.appendCircle(handleCircle,numPoints);
        buidler.appendOpenCylinder(handleCylinder,numPoints);
        return buidler.build();
    }

}
