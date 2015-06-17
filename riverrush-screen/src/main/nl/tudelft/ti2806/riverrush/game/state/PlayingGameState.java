package nl.tudelft.ti2806.riverrush.game.state;

import com.badlogic.gdx.Gdx;
import nl.tudelft.ti2806.riverrush.domain.entity.AbstractAnimal;
import nl.tudelft.ti2806.riverrush.domain.entity.Sector;
import nl.tudelft.ti2806.riverrush.domain.event.AddObstacleEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AddRockEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalAddedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalDroppedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalFellOffEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalJumpedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalMovedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.AnimalReturnedToBoatEvent;
import nl.tudelft.ti2806.riverrush.domain.event.BoatCollidedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.Direction;
import nl.tudelft.ti2806.riverrush.domain.event.Event;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.HandlerLambda;
import nl.tudelft.ti2806.riverrush.domain.event.TeamProgressEvent;
import nl.tudelft.ti2806.riverrush.game.Game;
import nl.tudelft.ti2806.riverrush.game.TickHandler;
import nl.tudelft.ti2806.riverrush.graphics.entity.Animal;
import nl.tudelft.ti2806.riverrush.graphics.entity.AnimalActor;
import nl.tudelft.ti2806.riverrush.graphics.entity.BoatGroup;
import nl.tudelft.ti2806.riverrush.graphics.entity.CannonBallGraphic;
import nl.tudelft.ti2806.riverrush.graphics.entity.RockGraphic;
import nl.tudelft.ti2806.riverrush.graphics.entity.Team;
import nl.tudelft.ti2806.riverrush.screen.PlayingGameScreen;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * State for a game that is playing.
 */
public class PlayingGameState extends AbstractGameState {

    private final PlayingGameScreen screen;
    private final HandlerLambda<AddObstacleEvent> addObstacleEventHandlerLambda = this::addObstacle;
    private final HandlerLambda<AddRockEvent> addRockEventHandlerLambda = this::addRock;
    private final HandlerLambda<AnimalAddedEvent> addAnimalHandlerLambda = this::addAnimalHandler;
    private final HandlerLambda<AnimalDroppedEvent> animalDroppedEventHandlerLambda = this::animalDropHandler;
    private final HandlerLambda<AnimalFellOffEvent> animalFellOffEventHandlerLambda = this::animalFellOff;
    private final HandlerLambda<AnimalJumpedEvent> animalJumpedEventHandlerLambda = this::animalJumpHandler;
    private final HandlerLambda<AnimalMovedEvent> animalMovedHandlerLambda = this::animalMoveHandler;
    private final HandlerLambda<AnimalReturnedToBoatEvent> animalReturnedToBoatEventHandlerLambda = this::animalReturnedToBoat;
    private final HandlerLambda<TeamProgressEvent> teamProgressEventHandlerLambda = this::teamProgress;

    private final TickHandler onTick = this::tick;

    private final HashMap<Integer, ArrayList<RockGraphic>> rocks;
    private final HashMap<Integer, ArrayList<CannonBallGraphic>> obstacles;

    /**
     * The state of the game that indicates that the game is currently playable.
     *
     * @param eventDispatcher the dispatcher that is used to handle any relevant events for the game
     *            in this state.
     * @param game refers to the game that this state belongs to.
     */
    public PlayingGameState(final EventDispatcher eventDispatcher, final Game game) {
        super(eventDispatcher, game);

        this.screen = new PlayingGameScreen(this.onTick);

        Gdx.app.postRunnable(() -> {
            PlayingGameState.this.game.setScreen(PlayingGameState.this.screen);

            for (Team currentTeam : PlayingGameState.this.game.getTeams()) {
                PlayingGameState.this.addBoat(currentTeam);
                for (AbstractAnimal currentAnimal : currentTeam.getAnimals()) {
                    PlayingGameState.this.addAnimal(currentTeam, (Animal) currentAnimal);
                }
            }
        });

        this.dispatcher.attach(AddObstacleEvent.class, this.addObstacleEventHandlerLambda);
        this.dispatcher.attach(AddRockEvent.class, this.addRockEventHandlerLambda);
        this.dispatcher.attach(AnimalAddedEvent.class, this.addAnimalHandlerLambda);
        this.dispatcher.attach(AnimalDroppedEvent.class, this.animalDroppedEventHandlerLambda);
        this.dispatcher.attach(AnimalFellOffEvent.class, this.animalFellOffEventHandlerLambda);
        this.dispatcher.attach(AnimalJumpedEvent.class, this.animalJumpedEventHandlerLambda);
        this.dispatcher.attach(AnimalMovedEvent.class, this.animalMovedHandlerLambda);
        this.dispatcher.attach(AnimalReturnedToBoatEvent.class,
                this.animalReturnedToBoatEventHandlerLambda);
        this.dispatcher.attach(TeamProgressEvent.class, this.teamProgressEventHandlerLambda);

        this.rocks = new HashMap<>();
        this.obstacles = new HashMap<>();
    }

    @Override
    public void dispose() {
        this.dispatcher.detach(AddObstacleEvent.class, this.addObstacleEventHandlerLambda);
        this.dispatcher.detach(AddRockEvent.class, this.addRockEventHandlerLambda);
        this.dispatcher.detach(AnimalAddedEvent.class, this.addAnimalHandlerLambda);
        this.dispatcher.detach(AnimalDroppedEvent.class, this.animalDroppedEventHandlerLambda);
        this.dispatcher.detach(AnimalFellOffEvent.class, this.animalFellOffEventHandlerLambda);
        this.dispatcher.detach(AnimalJumpedEvent.class, this.animalJumpedEventHandlerLambda);
        this.dispatcher.detach(AnimalMovedEvent.class, this.animalMovedHandlerLambda);
        this.dispatcher.detach(AnimalReturnedToBoatEvent.class,
                this.animalReturnedToBoatEventHandlerLambda);
        this.dispatcher.detach(TeamProgressEvent.class, this.teamProgressEventHandlerLambda);
        this.screen.dispose();
    }

    /**
     * This method is called when the game renders the screen.
     */
    private void tick() {
        for (Team team : this.game.getTeams()) {
            this.updateRockCollision(this.rocks.get(team.getId()), team);
            this.updateObstacleCollision(this.obstacles.get(team.getId()), team);
        }

    }

    private synchronized void updateObstacleCollision(final ArrayList<CannonBallGraphic> obstacles,
            final Team team) {
        if (obstacles == null) {
            return;
        }
        for (CannonBallGraphic graphic : obstacles) {
            BoatGroup boat = team.getBoat();
            if (boat.isColliding(graphic.getBounds())) {
                AnimalActor hitByCollision = boat.getCollidingChild(graphic.getBounds());
                if (hitByCollision != null) {
                    hitByCollision.getAnimal().collide();
                }
            }
        }
    }

    private synchronized void updateRockCollision(final ArrayList<RockGraphic> rocks,
            final Team team) {
        if (rocks == null) {
            return;
        }
        for (RockGraphic graphic : rocks) {
            BoatGroup boat = team.getBoat();

            if (boat.isColliding(graphic.getBounds())) {
                BoatCollidedEvent event = new BoatCollidedEvent();
                event.setTeam(team.getId());
                event.setDirection(graphic.getDirection());
                this.dispatcher.dispatch(event);
            }
        }
    }

    /**
     * Is called when an obstacle event is received.
     *
     * @param e - The event
     */
    private synchronized void addObstacle(final AddObstacleEvent e) {
        CannonBallGraphic graphic = new CannonBallGraphic(e.getLocation());
        this.screen.addObstacle(e.getTeam() == 0, graphic);
        ArrayList<CannonBallGraphic> obs = this.obstacles.getOrDefault(e.getTeam(),
                new ArrayList<>());
        obs.add(graphic);
        this.obstacles.put(e.getTeam(), obs);
    }

    /**
     * Is called when an obstacle event is received.
     *
     * @param e - The event
     */
    private synchronized void addRock(final AddRockEvent e) {
        RockGraphic graphic = new RockGraphic(e.getLocation());
        this.screen.addRock(e.getTeam() == 0, graphic);
        ArrayList<RockGraphic> obs = this.rocks.getOrDefault(e.getTeam(), new ArrayList<>());
        obs.add(graphic);
        this.rocks.put(e.getTeam(), obs);
    }

    /**
     * Adds an animal to a team.
     *
     * @param team The animal
     * @param animal The team
     */
    private void addAnimal(final Team team, final Animal animal) {
        AnimalActor actor = new AnimalActor(this.dispatcher, team.getId());
        animal.setActor(actor);
        actor.setAnimal(animal);

        team.addAnimal(animal);
        team.getBoat().addAnimal(actor, animal.getSectorOnBoat());

        actor.init();
    }

    /**
     * Add a boat to the game.
     *
     * @param team The team belonging to that boat
     */
    public void addBoat(final Team team) {
        this.screen.addBoat(team);
    }

    /**
     * Add an animal.
     *
     * @param event The add event
     */
    public void addAnimalHandler(final AnimalAddedEvent event) {
        Integer animalId = event.getAnimal();
        Integer teamId = event.getTeam();
        Integer variation = event.getVariation();
        Sector sector = event.getSector();

        Team team = this.game.getTeam(teamId);
        Animal animal = new Animal(this.dispatcher, animalId, teamId, variation, sector);

        this.addAnimal(team, animal);
    }

    /**
     * Tells a given animal to perform the jump action.
     *
     * @param event The jump event
     */
    public void animalJumpHandler(final AnimalJumpedEvent event) {
        Integer teamId = event.getTeam();
        Team team = this.game.getTeam(teamId);

        Integer animalId = event.getAnimal();
        AbstractAnimal animal = team.getAnimal(animalId);

        animal.jump();
    }

    /**
     * Move an animal on the boat.
     *
     * @param event The event
     */
    public void animalMoveHandler(final AnimalMovedEvent event) {
        Integer teamId = event.getTeam();
        Team team = this.game.getTeam(teamId);

        Integer animalId = event.getAnimal();
        AbstractAnimal animal = team.getAnimal(animalId);

        Direction direction = event.getDirection();
        animal.voteOneDirection(direction);

        if (direction == Direction.LEFT) {
            team.getBoat().voteForDirection(animal, -1);
        } else if (direction == Direction.RIGHT) {
            team.getBoat().voteForDirection(animal, 1);
        }
    }

    /**
     * Tells a given animal to perform the drop action.
     *
     * @param event The drop event
     */
    public void animalDropHandler(final AnimalDroppedEvent event) {
        Integer teamId = event.getTeam();
        Team team = this.game.getTeam(teamId);

        Integer animalId = event.getAnimal();
        AbstractAnimal animal = team.getAnimal(animalId);

        animal.drop();
    }

    /**
     * Is called when there is a team update on the progress.
     *
     * @param teamProgressEvent - The event
     */
    private void teamProgress(final TeamProgressEvent teamProgressEvent) {
        Integer teamId = teamProgressEvent.getTeam();
        Double progress = teamProgressEvent.getProgress();
        Double speed = teamProgressEvent.getSpeed();

        this.screen.updateProgress(teamId, progress, speed);
    }

    /**
     * Kicks the animal off the boat.
     *
     * @param event - The event
     */
    private void animalFellOff(final AnimalFellOffEvent event) {
        Integer teamId = event.getTeam();
        Integer animalId = event.getAnimal();

        this.game.getTeam(teamId).getAnimal(animalId).fall();
    }

    /**
     * Moves the animal back to the boat.
     *
     * @param event - the event
     */
    private void animalReturnedToBoat(final AnimalReturnedToBoatEvent event) {
        Integer teamId = event.getTeam();
        Team team = this.game.getTeam(teamId);

        Integer animalId = event.getAnimal();
        AbstractAnimal animal = team.getAnimal(animalId);

        animal.returnToBoat();
    }

    @Override
    public Event getStateEvent() {
        return null;
    }

    @Override
    public GameState start() {
        return this;
    }

    @Override
    public GameState stop() {
        this.screen.dispose();
        return new StoppedGameState(this.dispatcher, this.game);
    }

    @Override
    public GameState finish(final Integer team) {
        this.screen.dispose();
        return new FinishedGameState(this.dispatcher, this.game, team);
    }

    @Override
    public GameState waitForPlayers() {
        return this;
    }
}