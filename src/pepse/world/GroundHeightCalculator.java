package pepse.world;
@FunctionalInterface
/**
 * a functional interface that is responsible for calculation the ground height at a certain coordinate x
 */
public interface GroundHeightCalculator {
    public float GroundHeightAt(float x);
}
