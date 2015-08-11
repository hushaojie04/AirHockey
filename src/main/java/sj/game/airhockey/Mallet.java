package sj.game.airhockey;

import android.opengl.GLES20;

import java.util.List;

/**
 * Created by Administrator on 2015/8/11.
 */
public class Mallet {
    private static final int COLOR_COMPONENT_COUNT = 3;
    public  final float radius;
    public final float height;
    private final VertexArray vertexArray;
    private final List<ObjectBuidler.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuidler.GeneratedData generatedData = ObjectBuidler.createMallet(new Geometry.Point(0f, 0f, 0f),
                radius, height, numPointsAroundMallet);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionAttributeLocation(),
                COLOR_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (ObjectBuidler.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
