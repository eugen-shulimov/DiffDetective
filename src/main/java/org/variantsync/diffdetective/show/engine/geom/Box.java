package org.variantsync.diffdetective.show.engine.geom;

public record Box(Vec2 upperLeft, Vec2 lowerRight) {
    public double getWidth() {
        return lowerRight.x() - upperLeft().x();
    }

    public double getHeight() {
        return lowerRight.y() - upperLeft.y();
    }

    public Box shrink(Vec2 delta) {
        return new Box(
                this.upperLeft().add(delta),
                this.lowerRight.minus(delta)
        );
    }

    public Box shrink(double delta) {
        return shrink(new Vec2(delta, delta));
    }
}
