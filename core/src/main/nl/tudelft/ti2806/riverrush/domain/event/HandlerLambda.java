package nl.tudelft.ti2806.riverrush.domain.event;

@FunctionalInterface
public interface HandlerLambda {
    void handle(Event event);
}
