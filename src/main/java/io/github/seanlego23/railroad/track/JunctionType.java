package io.github.seanlego23.railroad.track;

import com.google.common.collect.ImmutableMap;
import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.RailroadException;
import io.github.seanlego23.railroad.connection.Connector;
import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.util.target.RUID;
import io.github.seanlego23.railroad.util.world.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.*;

public final class JunctionType implements Cloneable, Externalizable {

    private enum Orientation {
        LEFT(Direction.EAST),
        LEFT_UP(Direction.EAST_UP),
        LEFT_DOWN(Direction.EAST_DOWN),
        FORWARD(Direction.SOUTH),
        FORWARD_UP(Direction.SOUTH_UP),
        FORWARD_DOWN(Direction.SOUTH_DOWN),
        RIGHT(Direction.WEST),
        RIGHT_UP(Direction.WEST_UP),
        RIGHT_DOWN(Direction.WEST_DOWN),
        BACKWARD(Direction.NORTH),
        BACKWARD_UP(Direction.NORTH_UP),
        BACKWARD_DOWN(Direction.NORTH_DOWN);

        private final Direction direction;

        Orientation(Direction direction) {
            this.direction = direction;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public Orientation rotateCC() {
            switch (this) {
                case LEFT:
                    return BACKWARD;
                case LEFT_UP:
                    return BACKWARD_UP;
                case LEFT_DOWN:
                    return BACKWARD_DOWN;
                case RIGHT:
                    return FORWARD;
                case RIGHT_UP:
                    return FORWARD_UP;
                case RIGHT_DOWN:
                    return FORWARD_DOWN;
                case FORWARD:
                    return LEFT;
                case FORWARD_UP:
                    return LEFT_UP;
                case FORWARD_DOWN:
                    return LEFT_DOWN;
                case BACKWARD:
                    return RIGHT;
                case BACKWARD_UP:
                    return RIGHT_UP;
                default:
                    return RIGHT_DOWN;
            }
        }

        public Orientation rotate180() {
            switch (this) {
                case LEFT:
                    return RIGHT;
                case LEFT_UP:
                    return RIGHT_UP;
                case LEFT_DOWN:
                    return RIGHT_DOWN;
                case RIGHT:
                    return LEFT;
                case RIGHT_UP:
                    return LEFT_UP;
                case RIGHT_DOWN:
                    return LEFT_DOWN;
                case FORWARD:
                    return BACKWARD;
                case FORWARD_UP:
                    return BACKWARD_UP;
                case FORWARD_DOWN:
                    return BACKWARD_DOWN;
                case BACKWARD:
                    return FORWARD;
                case BACKWARD_UP:
                    return FORWARD_UP;
                default:
                    return FORWARD_DOWN;
            }
        }

        public Orientation rotateC() {
            switch (this) {
                case LEFT:
                    return FORWARD;
                case LEFT_UP:
                    return FORWARD_UP;
                case LEFT_DOWN:
                    return FORWARD_DOWN;
                case RIGHT:
                    return BACKWARD;
                case RIGHT_UP:
                    return BACKWARD_UP;
                case RIGHT_DOWN:
                    return BACKWARD_DOWN;
                case FORWARD:
                    return RIGHT;
                case FORWARD_UP:
                    return RIGHT_UP;
                case FORWARD_DOWN:
                    return RIGHT_DOWN;
                case BACKWARD:
                    return LEFT;
                case BACKWARD_UP:
                    return LEFT_UP;
                default:
                    return LEFT_DOWN;
            }
        }

        public Orientation getUp() {
            switch (this) {
                case LEFT:
                case LEFT_UP:
                case LEFT_DOWN:
                    return LEFT_UP;
                case RIGHT:
                case RIGHT_UP:
                case RIGHT_DOWN:
                    return RIGHT_UP;
                case FORWARD:
                case FORWARD_UP:
                case FORWARD_DOWN:
                    return FORWARD_UP;
                default:
                    return BACKWARD_UP;
            }
        }

        public Orientation getDown() {
            switch (this) {
                case LEFT:
                case LEFT_UP:
                case LEFT_DOWN:
                    return LEFT_DOWN;
                case RIGHT:
                case RIGHT_UP:
                case RIGHT_DOWN:
                    return RIGHT_DOWN;
                case FORWARD:
                case FORWARD_UP:
                case FORWARD_DOWN:
                    return FORWARD_DOWN;
                default:
                    return BACKWARD_DOWN;
            }
        }

        public Orientation getRegular() {
            switch (this) {
                case LEFT:
                case LEFT_UP:
                case LEFT_DOWN:
                    return LEFT;
                case RIGHT:
                case RIGHT_UP:
                case RIGHT_DOWN:
                    return RIGHT;
                case FORWARD:
                case FORWARD_UP:
                case FORWARD_DOWN:
                    return FORWARD;
                default:
                    return BACKWARD;
            }
        }

        public boolean isRegular() {
            switch (this) {
                case LEFT:
                case RIGHT:
                case FORWARD:
                case BACKWARD:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isDown() {
            switch (this) {
                case LEFT_DOWN:
                case RIGHT_DOWN:
                case FORWARD_DOWN:
                case BACKWARD_DOWN:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isUp() {
            switch (this) {
                case LEFT_UP:
                case RIGHT_UP:
                case FORWARD_UP:
                case BACKWARD_UP:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isSameDirection(Orientation other) {
            switch (this) {
                case LEFT:
                case LEFT_UP:
                case LEFT_DOWN:
                    return other == LEFT || other == LEFT_UP || other == LEFT_DOWN;
                case RIGHT:
                case RIGHT_UP:
                case RIGHT_DOWN:
                    return other == RIGHT || other == RIGHT_UP || other == RIGHT_DOWN;
                case FORWARD:
                case FORWARD_UP:
                case FORWARD_DOWN:
                    return other == FORWARD || other == FORWARD_UP || other == FORWARD_DOWN;
                default:
                    return other == BACKWARD || other == BACKWARD_UP || other == BACKWARD_DOWN;
            }
        }

        public byte encode() {
            byte code;

            if (this.isSameDirection(FORWARD))
                code = 0x01;
            else if (this.isSameDirection(LEFT))
                code = 0x02;
            else if (this.isSameDirection(BACKWARD))
                code = 0x04;
            else
                code = 0x08;

            if (this.isUp())
                code += 0x10;
            else if (this.isDown())
                code += 0x20;

            return code;
        }
    }

    private static final long serialVersionUID = -5672603798303129964L;
    private static final ImmutableMap<Integer, JunctionType> allTypes;

    public static final JunctionType T;
    public static final JunctionType LLT;
    public static final JunctionType FLT;
    public static final JunctionType RLT;
    public static final JunctionType L2LT;
    public static final JunctionType O2LT;
    public static final JunctionType R2LT;
    public static final JunctionType LT;
    public static final JunctionType LRT;
    public static final JunctionType RRT;
    public static final JunctionType LR_FLT;
    public static final JunctionType LR_RLT;
    public static final JunctionType LR_R2LT;
    public static final JunctionType RR_LLT;
    public static final JunctionType RR_FLT;
    public static final JunctionType RR_L2LT;
    public static final JunctionType F;
    public static final JunctionType L1F;
    public static final JunctionType A2LF;
    public static final JunctionType O2LF;
    public static final JunctionType L3F;
    public static final JunctionType LF;
    public static final JunctionType R1F;
    public static final JunctionType R1RLF;
    public static final JunctionType R1OLF;
    public static final JunctionType R1LLF;
    public static final JunctionType R1RA2LF;
    public static final JunctionType R1O2LF;
    public static final JunctionType R1LA2F;
    public static final JunctionType R1L3F;
    public static final JunctionType R2F;
    public static final JunctionType R2RLF;
    public static final JunctionType R2LLF;
    public static final JunctionType R2L2F;

    static {
        T = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT);
        LLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT);
        FLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT);
        RLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_DOWN);
        L2LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT);
        O2LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN);
        R2LT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN);
        LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN);
        LRT = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT);
        RRT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_UP);
        LR_FLT = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT);
        LR_RLT = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT_DOWN);
        LR_R2LT = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN);
        RR_LLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_UP);
        RR_FLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP);
        RR_L2LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP);
        F = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD);
        L1F = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD);
        A2LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD);
        O2LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        L3F = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        R1F = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD);
        R1RLF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD);
        R1OLF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        R1LLF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        R1RA2LF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        R1O2LF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        R1LA2F = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        R1L3F = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        R2F = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_UP, Orientation.RIGHT, Orientation.BACKWARD);
        R2RLF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_UP, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        R2LLF = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_UP, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        R2L2F = new JunctionType(Orientation.LEFT_UP, Orientation.FORWARD_UP, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);

        allTypes = ImmutableMap.<Integer, JunctionType>builder()
                .put(T.encode(), T).put(LLT.encode(), LLT).put(FLT.encode(), FLT).put(RLT.encode(), RLT)
                .put(L2LT.encode(), L2LT).put(O2LT.encode(), O2LT).put(R2LT.encode(), R2LT).put(LT.encode(), LT)
                .put(LRT.encode(), LRT).put(RRT.encode(), RRT).put(LR_FLT.encode(), LR_FLT).put(LR_RLT.encode(), LR_RLT)
                .put(LR_R2LT.encode(), LR_R2LT).put(RR_LLT.encode(), RR_LLT).put(RR_FLT.encode(), RR_FLT)
                .put(RR_L2LT.encode(), RR_L2LT)
                .put(F.encode(), F).put(L1F.encode(), L1F).put(A2LF.encode(), A2LF).put(O2LF.encode(), O2LF)
                .put(L3F.encode(), L3F).put(LF.encode(), LF).put(R1F.encode(), R1F).put(R1RLF.encode(), R1RLF)
                .put(R1OLF.encode(), R1OLF).put(R1LLF.encode(), R1LLF).put(R1RA2LF.encode(), R1RA2LF)
                .put(R1O2LF.encode(), R1O2LF).put(R1LA2F.encode(), R1LA2F).put(R1L3F.encode(), R1L3F)
                .put(R2F.encode(), R2F).put(R2RLF.encode(), R2RLF).put(R2LLF.encode(), R2LLF)
                .put(R2L2F.encode(), R2L2F).build();

    }

    private final Pair<Orientation, Boolean> leftOrientation;
    private final Pair<Orientation, Boolean> forwardOrientation;
    private final Pair<Orientation, Boolean> rightOrientation;
    private final Pair<Orientation, Boolean> backwardOrientation;
    private final Map<Orientation, Set<Orientation>> restrictions;
    private final RUID left;
    private final RUID forward;
    private final RUID right;
    private final RUID backward;
    private final Rotation rotation;

    private static Field leftOrientationField;
    private static Field forwardOrientationField;
    private static Field rightOrientationField;
    private static Field backwardOrientationField;
    private static Field restrictionsField;
    private static Field leftField;
    private static Field forwardField;
    private static Field rightField;
    private static Field backwardField;
    private static Field rotationField;

    static {
        try {
            leftOrientationField = JunctionType.class.getDeclaredField("leftOrientation");
            forwardOrientationField = JunctionType.class.getDeclaredField("forwardOrientation");
            rightOrientationField = JunctionType.class.getDeclaredField("forwardOrientation");
            backwardOrientationField = JunctionType.class.getDeclaredField("backwardOrientation");
            restrictionsField = JunctionType.class.getDeclaredField("restrictions");
            leftField = JunctionType.class.getDeclaredField("left");
            forwardField = JunctionType.class.getDeclaredField("forward");
            rightField = JunctionType.class.getDeclaredField("right");
            backwardField = JunctionType.class.getDeclaredField("backward");
            rotationField = JunctionType.class.getDeclaredField("rotation");
            leftOrientationField.setAccessible(true);
            forwardOrientationField.setAccessible(true);
            rightOrientationField.setAccessible(true);
            backwardOrientationField.setAccessible(true);
            restrictionsField.setAccessible(true);
            leftField.setAccessible(true);
            forwardField.setAccessible(true);
            rightField.setAccessible(true);
            backwardField.setAccessible(true);
            rotationField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            System.err.println("ERROR: The following should not have occurred. Recommend disabling Railroad plugin.");
            e.printStackTrace();
        }
    }

    public JunctionType() {
        this.leftOrientation = null;
        this.forwardOrientation = null;
        this.rightOrientation = null;
        this.backwardOrientation = null;
        this.restrictions = null;
        this.left = null;
        this.forward = null;
        this.right = null;
        this.backward = null;
        this.rotation = Rotation.NONE;
    }

    private JunctionType(@NotNull Orientation left, @NotNull Orientation forward,
                           @NotNull Orientation right) {
        this(left, forward, right, null);
    }


    private JunctionType(@NotNull Orientation left, @NotNull Orientation forward, @NotNull Orientation right,
                           @Nullable Orientation backward) {
        this.leftOrientation = new Pair<>(left, left.isDown());
        this.forwardOrientation = new Pair<>(forward, forward.isDown());
        this.rightOrientation = new Pair<>(right, right.isDown());
        if (backward == null)
            this.backwardOrientation = null;
        else
            this.backwardOrientation = new Pair<>(backward, backward.isDown());
        this.left = null;
        this.forward = null;
        this.right = null;
        this.backward = null;
        this.rotation = Rotation.NONE;

        this.restrictions = new HashMap<>();
        if (left.isUp()) {
            this.setRestrictions(left, forward);
            if (backward != null)
                this.setRestrictions(left, backward);
        }
        if (forward.isUp()) {
            this.setRestrictions(forward, left);
            this.setRestrictions(forward, right);
        }
        if (right.isUp()) {
            this.setRestrictions(right, forward);
            if (backward != null)
                this.setRestrictions(right, backward);
        }
        if (backward != null && backward.isUp()) {
            this.setRestrictions(backward, left);
            this.setRestrictions(backward, right);
        }
    }

    public static @NotNull JunctionType createJunctionType(@NotNull Location loc, @NotNull Connector first,
                                                           @NotNull Connector second, @NotNull Connector third,
                                                           @Nullable Connector fourth)
            throws InvalidJunctionLocationException {

        List<Pair<Connector, Orientation>> orientations = new ArrayList<>();

        if (loc.getBlock().getType() != Material.RAIL)
            throw new InvalidJunctionLocationException("The location given does not contain a normal rail material.");
        Orientation orient = Orientation.LEFT;
        Location var;
        for (int i = 0; i < 4; i++) {
            var = loc.clone();
            var.add(orient.getDirection().getVector());

            boolean found = false;
            if (Rail.materialIsRail(var.getBlock().getType())) {
                if (first.containsRail(var)) {
                    orientations.add(new Pair<>(first, orient));
                    found = true;
                } else if (second.containsRail(var)) {
                    orientations.add(new Pair<>(second, orient));
                    found = true;
                } else if (third.containsRail(var)) {
                    orientations.add(new Pair<>(third, orient));
                    found = true;
                } else if (fourth != null && fourth.containsRail(var)) {
                    orientations.add(new Pair<>(fourth, orient));
                    found = true;
                }
            }
            if (found)
                orient = orient.getRegular().rotateC();
            else {
                if (orient.isRegular()) {
                    orient = orient.getDown();
                    i -= 1;
                } else if (orient.isDown()) {
                    orient = orient.getUp();
                    i -= 1;
                } else {
                    orient = orient.getRegular().rotateC();
                }
            }
        }

        if (orientations.size() < 3 || (fourth != null && orientations.size() != 4))
            throw new InvalidJunctionLocationException("Some of the tracks were not found.");
        for (Pair<Connector, Orientation> pair : orientations) {
            if (pair.getSecond().isDown()) {
                Location railLoc = loc.clone().add(pair.getSecond().getDirection().getVector());
                Rail rail = pair.getFirst().getRail(railLoc);
                if (rail == null)
                    throw new RailroadException("ITrack: " + pair.getFirst().getID().toString() + " Location: " +
                            railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                            " Error: Couldn't find a rail at this location.");
                if (pair.getFirst() instanceof ITrack) {
                    ITrack track = (ITrack) pair.getFirst();
                    if (!track.getStart().equals(rail) && !track.getEnd().equals(rail) &&
                            rail.getDefaultShape().isCurved())
                        throw new InvalidJunctionLocationException("ITrack: " + pair.getFirst().getID().toString() +
                                " Location: " + railLoc.getBlockX() + "," + railLoc.getBlockY() + "," +
                                railLoc.getBlockZ() + " Error: Cannot connect to junction from below at a corner of " +
                                "the track.");
                }
            }
        }

        Orientation firstOrient = orientations.get(0).getSecond();
        Orientation secondOrient = orientations.get(1).getSecond();
        Orientation thirdOrient = orientations.get(2).getSecond();
        Orientation fourthOrient = fourth == null ? null : orientations.get(3).getSecond();
        first = orientations.get(0).getFirst();
        second = orientations.get(1).getFirst();
        third = orientations.get(2).getFirst();
        fourth = fourthOrient == null ? null : orientations.get(3).getFirst();

        int orig, rotCC, flip, rotC;
        orig = encode(firstOrient, secondOrient, thirdOrient, fourthOrient);
        rotCC = encode(firstOrient.rotateCC(), secondOrient.rotateCC(), thirdOrient.rotateCC(),
                fourthOrient == null ? null: fourthOrient.rotateCC());
        flip = encode(firstOrient.rotate180(), secondOrient.rotate180(), thirdOrient.rotate180(),
                fourthOrient == null ? null : fourthOrient.rotate180());
        rotC = encode(firstOrient.rotateC(), secondOrient.rotateC(), thirdOrient.rotateC(),
                fourthOrient == null ? null : fourthOrient.rotateC());

        Rotation rotation = null;
        JunctionType junctionType = null;
        if (allTypes.containsKey(orig)) {
            rotation = Rotation.NONE;
            junctionType = allTypes.get(orig);
        } else if (allTypes.containsKey(rotCC)) {
            rotation = Rotation.CC90;
            junctionType = allTypes.get(rotCC);
        } else if (allTypes.containsKey(flip)) {
            rotation = Rotation.FLIP;
            junctionType = allTypes.get(flip);
        } else if (allTypes.containsKey(rotC)) {
            rotation = Rotation.C90;
            junctionType = allTypes.get(rotC);
        }

        if (junctionType == null)
            throw new InvalidJunctionLocationException("Junction type is invalid.");

        try {
            leftField.set(junctionType, orientations.get(0).getFirst().getID());
            forwardField.set(junctionType, orientations.get(1).getFirst().getID());
            rightField.set(junctionType, orientations.get(2).getFirst().getID());
            backwardField.set(junctionType, fourth == null ? null : orientations.get(3).getFirst().getID());
            rotationField.set(junctionType, rotation);
        } catch (IllegalAccessException e) {
            throw new InvalidJunctionLocationException("Creating a JunctionType failed.", e);
        }

        if (first instanceof ITrack) {
            ITrack track = (ITrack) first;
            if (track.getWay() != ITrack.Way.BOTH_WAYS) {
                Location railLoc = loc.clone().add(firstOrient.getDirection().getVector());
                Rail rail = track.getRail(railLoc);
                if (rail == null)
                    throw new RailroadException("ITrack: " + track.getID().toString() + " Location: " +
                            railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                            " Error: Couldn't find a rail at this location.");
                if ((track.getStart().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_TO) ||
                        (track.getEnd().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_FROM)) {
                    junctionType.setRestriction(secondOrient, firstOrient);
                    junctionType.setRestriction(thirdOrient, firstOrient);
                    if (fourth != null)
                        junctionType.setRestriction(fourthOrient, firstOrient);
                }
            }
        }
        if (second instanceof ITrack) {
            ITrack track = (ITrack) second;
            if (track.getWay() != ITrack.Way.BOTH_WAYS) {
                Location railLoc = loc.clone().add(secondOrient.getDirection().getVector());
                Rail rail = track.getRail(railLoc);
                if (rail == null)
                    throw new RailroadException("ITrack: " + track.getID().toString() + " Location: " +
                            railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                            " Error: Couldn't find a rail at this location.");
                if ((track.getStart().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_TO) ||
                        (track.getEnd().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_FROM)) {
                    junctionType.setRestriction(firstOrient, secondOrient);
                    junctionType.setRestriction(thirdOrient, secondOrient);
                    if (fourth != null)
                        junctionType.setRestriction(fourthOrient, secondOrient);
                }
            }
        }
        if (third instanceof ITrack) {
            ITrack track = (ITrack) third;
            if (track.getWay() != ITrack.Way.BOTH_WAYS) {
                Location railLoc = loc.clone().add(thirdOrient.getDirection().getVector());
                Rail rail = track.getRail(railLoc);
                if (rail == null)
                    throw new RailroadException("ITrack: " + track.getID().toString() + " Location: " +
                            railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                            " Error: Couldn't find a rail at this location.");
                if ((track.getStart().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_TO) ||
                        (track.getEnd().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_FROM)) {
                    junctionType.setRestriction(firstOrient, thirdOrient);
                    junctionType.setRestriction(secondOrient, thirdOrient);
                    if (fourth != null)
                        junctionType.setRestriction(fourthOrient, thirdOrient);
                }
            }
        }
        if (fourth instanceof ITrack) {
            ITrack track = (ITrack) fourth;
            if (track.getWay() != ITrack.Way.BOTH_WAYS) {
                Location railLoc = loc.clone().add(fourthOrient.getDirection().getVector());
                Rail rail = track.getRail(railLoc);
                if (rail == null)
                    throw new RailroadException("ITrack: " + track.getID().toString() + " Location: " +
                            railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                            " Error: Couldn't find a rail at this location.");
                if ((track.getStart().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_TO) ||
                        (track.getEnd().equals(rail) && track.getWay() == ITrack.Way.ONE_WAY_FROM)) {
                    junctionType.setRestriction(firstOrient, fourthOrient);
                    junctionType.setRestriction(secondOrient, fourthOrient);
                    junctionType.setRestriction(thirdOrient, fourthOrient);
                }
            }
        }

        if (junctionType.isRestricted(first.getID(), second.getID()) &&
            junctionType.isRestricted(first.getID(), third.getID()) &&
            (fourth == null || junctionType.isRestricted(first.getID(), fourth.getID())))
            throw new InvalidJunctionLocationException("Junction type is invalid.");
        if (junctionType.isRestricted(second.getID(), first.getID()) &&
            junctionType.isRestricted(second.getID(), third.getID()) &&
            (fourth == null || junctionType.isRestricted(second.getID(), fourth.getID())))
            throw new InvalidJunctionLocationException("Junction type is invalid.");
        if (junctionType.isRestricted(third.getID(), first.getID()) &&
            junctionType.isRestricted(third.getID(), second.getID()) &&
            (fourth == null || junctionType.isRestricted(third.getID(), fourth.getID())))
            throw new InvalidJunctionLocationException("Junction type is invalid.");
        if (fourth != null &&
            junctionType.isRestricted(fourth.getID(), first.getID()) &&
            junctionType.isRestricted(fourth.getID(), second.getID()) &&
            junctionType.isRestricted(fourth.getID(), third.getID()))
            throw new InvalidJunctionLocationException("Junction type is invalid.");

        return junctionType;
    }

    private void setRestriction(Orientation from, Orientation to) {
        if (!this.restrictions.containsKey(from))
            this.restrictions.put(from, new HashSet<>());
        this.restrictions.get(from).add(to);
    }

    private void setRestrictions(Orientation first, Orientation second) {
        if (!this.restrictions.containsKey(first))
            this.restrictions.put(first, new HashSet<>());
        this.restrictions.get(first).add(second);
        if (!this.restrictions.containsKey(second))
            this.restrictions.put(second, new HashSet<>());
        this.restrictions.get(second).add(first);
    }

    public boolean isRestricted(@NotNull RUID from, @NotNull RUID to) {
        Orientation orientFrom;
        Orientation orientTo;

        if (!(from.isValid() && to.isValid()))
            return false;

        if (from.equals(this.left))
            orientFrom = this.leftOrientation.getFirst();
        else if (from.equals(this.forward))
            orientFrom = this.forwardOrientation.getFirst();
        else if (from.equals(this.right))
            orientFrom = this.rightOrientation.getFirst();
        else if (from.equals(this.backward))
            orientFrom = this.backwardOrientation.getFirst();
        else
            return false;

        if (to.equals(this.left))
            orientTo = this.leftOrientation.getFirst();
        else if (to.equals(this.forward))
            orientTo = this.forwardOrientation.getFirst();
        else if (to.equals(this.right))
            orientTo = this.rightOrientation.getFirst();
        else if (to.equals(this.backward))
            orientTo = this.backwardOrientation.getFirst();
        else
            return false;

        if (orientFrom == orientTo)
            return false;

        if (!this.restrictions.containsKey(orientFrom))
            return false;

        return this.restrictions.get(orientFrom).contains(orientTo);
    }

    public @Nullable Rail.Shape sideChange(RUID side) {
        if (this.left.equals(side))
            return Rail.Shape.valueOf("ASCENDING_" + this.leftOrientation.getFirst().getDirection().getOpposite()
                    .getRegular().name());
        else if (this.forward.equals(side))
            return Rail.Shape.valueOf("ASCENDING_" + this.forwardOrientation.getFirst().getDirection().getOpposite()
                    .getRegular().name());
        else if (this.right.equals(side))
            return Rail.Shape.valueOf("ASCENDING_" + this.rightOrientation.getFirst().getDirection().getOpposite()
                    .getRegular().name());
        else if (this.backward.equals(side))
            return Rail.Shape.valueOf("ASCENDING_" + this.backwardOrientation.getFirst().getDirection().getOpposite()
                    .getRegular().name());
        else
            return null;
    }

    public Rail.Shape centerShape(RUID from, RUID to) {
        if (!this.isRestricted(from, to))
            return null;

        Pair<Orientation, Orientation> orients = new Pair<>();

        if (this.left.equals(from))
            orients.setFirst(this.leftOrientation.getFirst());
        else if (this.forward.equals(from))
            orients.setFirst(this.forwardOrientation.getFirst());
        else if (this.right.equals(from))
            orients.setFirst(this.rightOrientation.getFirst());
        else if (this.backward.equals(from))
            orients.setFirst(this.backwardOrientation.getFirst());

        if (this.left.equals(to))
            orients.setSecond(this.leftOrientation.getFirst());
        else if (this.forward.equals(to))
            orients.setSecond(this.forwardOrientation.getFirst());
        else if (this.right.equals(to))
            orients.setSecond(this.rightOrientation.getFirst());
        else if (this.backward.equals(to))
            orients.setSecond(this.backwardOrientation.getFirst());

        if (orients.getFirst() == orients.getSecond())
            return null;

        if (orients.getFirst().rotate180().isSameDirection(orients.getSecond())) {
            if (orients.getFirst().isUp())
                return Rail.Shape.valueOf("ASCENDING_" + orients.getFirst().getDirection().getRegular().name());
            else if (orients.getSecond().isUp())
                return Rail.Shape.valueOf("ASCENDING_" + orients.getSecond().getDirection().getRegular().name());
            else {
                if (orients.getFirst().getDirection().onZAxis())
                    return Rail.Shape.NORTH_SOUTH;
                else
                    return Rail.Shape.EAST_WEST;
            }
        } else {
            if (orients.getFirst().getDirection().onZAxis())
                return Rail.Shape.valueOf(orients.getFirst().getDirection().getRegular().name() + "_" +
                                          orients.getSecond().getDirection().getRegular().name());
            else
                return Rail.Shape.valueOf(orients.getSecond().getDirection().getRegular().name() + "_" +
                                          orients.getFirst().getDirection().getRegular().name());
        }
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    protected Object clone() {
        JunctionType type = new JunctionType();
        if (this.leftOrientation == null)
            return type;
        try {
            leftOrientationField.set(type, new Pair<>(type.leftOrientation.getFirst(),
                    type.leftOrientation.getSecond()));
            forwardOrientationField.set(type, new Pair<>(type.forwardOrientation.getFirst(),
                    type.forwardOrientation.getSecond()));
            rightOrientationField.set(type, new Pair<>(type.rightOrientation.getFirst(),
                    type.rightOrientation.getSecond()));
            if (type.backwardOrientation != null)
                backwardOrientationField.set(type, new Pair<>(type.backwardOrientation.getFirst(),
                        type.backwardOrientation.getSecond()));
            Map<Orientation, Set<Orientation>> newRestrictions = new HashMap<>();
            for (Map.Entry<Orientation, Set<Orientation>> entry : type.restrictions.entrySet())
                newRestrictions.put(entry.getKey(), new HashSet<>(entry.getValue()));
            restrictionsField.set(type, newRestrictions);
            leftField.set(type, this.left);
            forwardField.set(type, this.forward);
            rightField.set(type, this.right);
            if (this.backward != null)
                backwardField.set(type, this.backward);
            rotationField.set(type, this.rotation);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new JunctionType();
        }

        return type;
    }

    public int encode() {
        int code = leftOrientation.getFirst().encode() + this.rotation.encode();
        code = forwardOrientation.getFirst().encode() + code << 8;
        code = rightOrientation.getFirst().encode() + code << 8;
        code = (backward == null ? 0 : rightOrientation.getFirst().encode()) + code << 8;
        return code;
    }

    private static int encode(@NotNull Orientation mainFirst, @NotNull Orientation mainSecond, @NotNull Orientation other1,
                       @Nullable Orientation other2) {
        int code = mainFirst.encode();
        code = mainSecond.encode() + code << 8;
        code = other1.encode() + code << 8;
        code = (other2 == null ? 0 : other2.encode()) + code << 8;
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof JunctionType))
            return false;
        JunctionType object = (JunctionType) o;

        if (this.left != object.left)
            return false;
        if (this.forward != object.forward)
            return false;
        if (this.right != object.right)
            return false;
        if (this.backward != object.backward)
            return false;
        if (this.leftOrientation.getFirst() != object.leftOrientation.getFirst())
            return false;
        if (!this.leftOrientation.getSecond().equals(object.leftOrientation.getSecond()))
            return false;
        if (this.forwardOrientation.getFirst() != object.forwardOrientation.getFirst())
            return false;
        if (!this.forwardOrientation.getSecond().equals(object.forwardOrientation.getSecond()))
            return false;
        if (this.rightOrientation.getFirst() != object.rightOrientation.getFirst())
            return false;
        if (!this.rightOrientation.getSecond().equals(object.rightOrientation.getSecond()))
            return false;
        if (this.backwardOrientation != null && object.backwardOrientation != null) {
            if (this.backwardOrientation.getFirst() != object.backwardOrientation.getFirst())
                return false;
            if (this.backwardOrientation.getSecond().equals(object.backwardOrientation.getSecond()))
                return false;
        } else if (this.backwardOrientation == null && object.backwardOrientation == null)
            return false;
        return this.rotation == object.rotation;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        this.writeHeader(out);

        this.writeFooter(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.readHeader(in);

        this.readFooter(in);
    }

    private void writeHeader(ObjectOutput out) throws IOException {
        out.writeChar('{');
        out.writeObject("JunctionType");
        out.writeObject(Railroad.version);
    }

    private void writeFooter(ObjectOutput out) throws IOException {
        out.writeChar('}');
    }

    private void readHeader(ObjectInput in) throws IOException, ClassNotFoundException {
        char left = in.readChar();
        if (left != '{')
            throw new ClassNotFoundException("'{' is missing from the start of JunctionType serialization.");

        String name = (String) in.readObject();
        if (!name.equals("JunctionType"))
            throw new ClassNotFoundException("Not a JunctionType object.");

        String ver = (String) in.readObject();
        if (!ver.equals(Railroad.version))
            throw new ClassNotFoundException("Not a valid JunctionType version.");
    }

    private void readFooter(ObjectInput in) throws IOException, ClassNotFoundException {
        char right = in.readChar();
        if (right != '}')
            throw new ClassNotFoundException("'}' is missing from the end of JunctionType serialization.");
    }
}
