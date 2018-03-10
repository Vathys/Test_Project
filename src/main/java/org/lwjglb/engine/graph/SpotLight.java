package org.lwjglb.engine.graph;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoff) {
	this.pointLight = pointLight;
	this.coneDirection = coneDirection;
	this.cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight) {
	this(spotLight.pointLight, spotLight.coneDirection, spotLight.cutoff);
    }

    public PointLight getPointLight() {
	return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
	this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
	return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
	this.coneDirection = coneDirection;
    }

    public float getCutoff() {
	return cutoff;
    }

    public void setCutoff(float cutoff) {
	this.cutoff = cutoff;
    }

    public final void setCutoffAngle(float cutoffAngle) {
	this.setCutoff((float) Math.cos(Math.toRadians(cutoffAngle)));
    }
}
