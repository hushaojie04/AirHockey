package sj.game.airhockey;

import android.opengl.GLES20;
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

//    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
//        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
//        ObjectBuidler buidler = new ObjectBuidler(size);
//        return buidler
//    }


}
