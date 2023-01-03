package pepse.world;
/**
 * a functional interface that is responsible for calculation the ground height at a certain coordinate x
 */
@FunctionalInterface
public interface GroundHeightCalculator {
    float GroundHeightAt(float x);
}
