package sj.game.airhockey;

import android.content.pm.ConfigurationInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/8/11.
 */
public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<ObjectBuidler.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuidler.GeneratedData generatedData = ObjectBuidler.createPuck(new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }
    public void bindData(ColorShaderProgram colorShaderProgram)
    {
        vertexArray.setVertexAttribPointer(0,colorShaderProgram.getPositionAttributeLocation(),POSITION_COMPONENT_COUNT,0);
    }
    public void draw()
    {
        for(ObjectBuidler.DrawCommand drawCommand:drawList)
        {
            drawCommand.draw();
        }
    }


}
