package nl.tudelft.ti2806.riverrush.network;

import nl.tudelft.ti2806.riverrush.controller.Controller;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.network.event.JoinEvent;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;
import org.mockito.Mockito;

import static com.google.inject.name.Names.named;

/**
 * Tests for the socket layer Server class.
 */
public class UserServerTest extends ServerTest {

    @Override
    public void setUp() {
        super.setUp();

        Mockito.when(this.protocolMock.deserialize("join"))
            .thenReturn(new JoinEvent());
    }

    /**
     * Configures injection of mocks.
     */
    @Override
    protected void configure() {
        // Every time a new EventDispatcher is requested by code under test,
        // Guice will inject a fresh mock.
        this.bind(EventDispatcher.class).toInstance(dispatcherMock);

        this.bind(Controller.class)
            .annotatedWith(named("clientController"))
            .toProvider(this.controllerProviderMock);

        this.bind(Protocol.class)
            .annotatedWith(named("clientProtocol"))
            .toInstance(this.protocolMock);

        this.bind(Server.class).to(UserServer.class);
    }
}
