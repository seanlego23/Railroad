package io.github.seanlego23.railroad.track;

import com.google.common.collect.ImmutableMap;
import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.RailroadException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JunctionType implements Cloneable, Externalizable {

    private enum Orientation {
        LEFT(Direction.EAST),
        LEFT_UP(Direction.EAST_UP),
        LEFT_DOWN(Direction.EAST_DOWN),
        RIGHT(Direction.WEST),
        RIGHT_UP(Direction.WEST_UP),
        RIGHT_DOWN(Direction.WEST_DOWN),
        FORWARD(Direction.SOUTH),
        FORWARD_UP(Direction.SOUTH_UP),
        FORWARD_DOWN(Direction.SOUTH_DOWN),
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

    private static final ImmutableMap<Integer, JunctionType> allTypes;

    public static final JunctionType T;
    public static final JunctionType LT;
    public static final JunctionType MSR_T;
    public static final JunctionType MSR_LT;
    public static final JunctionType MSR_B_T;
    public static final JunctionType MSR_B_LT;
    public static final JunctionType MSL_T;
    public static final JunctionType MSL_LT;
    public static final JunctionType MSL_B_T;
    public static final JunctionType MSL_B_LT;
    public static final JunctionType MLR_T;
    public static final JunctionType MLR_LT;
    public static final JunctionType MLR_B_T;
    public static final JunctionType MLR_B_LT;
    public static final JunctionType ML_T;
    public static final JunctionType ML_LT;
    public static final JunctionType F;
    public static final JunctionType SLF;
    public static final JunctionType LF;
    public static final JunctionType SRF;
    public static final JunctionType RLF;
    public static final JunctionType MSR_F;
    public static final JunctionType MSR_SLF;
    public static final JunctionType MSR_LF;
    public static final JunctionType MSR_SRF;
    public static final JunctionType MSR_RLF;
    public static final JunctionType MSR_B_SLF;
    public static final JunctionType MSR_B_SRF;
    public static final JunctionType MSR_B_RLF;
    public static final JunctionType MSL_F;
    public static final JunctionType MSL_SLF;
    public static final JunctionType MSL_LF;
    public static final JunctionType MSL_SRF;
    public static final JunctionType MSL_RLF;
    public static final JunctionType MSL_B_SLF;
    public static final JunctionType MSL_B_SRF;
    public static final JunctionType MSL_B_RLF;
    public static final JunctionType MLR_F;
    public static final JunctionType MLR_SLF;
    public static final JunctionType MLR_LF;
    public static final JunctionType MLR_SRF;
    public static final JunctionType MLR_RLF;
    public static final JunctionType MLR_B_SLF;
    public static final JunctionType MLR_B_SRF;
    public static final JunctionType MLR_B_RLF;
    public static final JunctionType ML_F;
    public static final JunctionType ML_SLF;
    public static final JunctionType ML_LF;
    public static final JunctionType ML_SRF;
    public static final JunctionType ML_RLF;
    public static final JunctionType CT;
    public static final JunctionType CLT;
    public static final JunctionType CRT;
    public static final JunctionType R_CT;
    public static final JunctionType R_CLT;
    public static final JunctionType R_CRT;
    public static final JunctionType MSL_CT;
    public static final JunctionType MSL_CLT;
    public static final JunctionType MSL_CRT;
    public static final JunctionType MSL_R_CT;
    public static final JunctionType MSL_R_CLT;
    public static final JunctionType MSL_R_CRT;
    public static final JunctionType MSL_B_CT;
    public static final JunctionType MSL_B_CLT;
    public static final JunctionType MSL_B_CRT;
    public static final JunctionType MSL_BR_CT;
    public static final JunctionType MSL_BR_CLT;
    public static final JunctionType MSL_BR_CRT;
    public static final JunctionType ML_CT;
    public static final JunctionType ML_CLT;
    public static final JunctionType ML_CRT;
    public static final JunctionType ML_R_CT;
    public static final JunctionType ML_R_CLT;
    public static final JunctionType ML_R_CRT;
    public static final JunctionType CF;
    public static final JunctionType SL_CF;
    public static final JunctionType CLF;
    public static final JunctionType SR_CF;
    public static final JunctionType RL_CF;
    public static final JunctionType CRF;
    public static final JunctionType R_SL_CF;
    public static final JunctionType R_SR_CF;
    public static final JunctionType R_RL_CF;
    public static final JunctionType MSL_CF;
    public static final JunctionType MSL_SL_CF;
    public static final JunctionType MSL_CLF;
    public static final JunctionType MSL_SR_CF;
    public static final JunctionType MSL_RL_CF;
    public static final JunctionType MSL_CRF;
    public static final JunctionType MSL_R_SL_CF;
    public static final JunctionType MSL_R_SR_CF;
    public static final JunctionType MSL_R_RL_CF;
    public static final JunctionType MSL_B_CF;
    public static final JunctionType MSL_B_SL_CF;
    public static final JunctionType MSL_B_CLF;
    public static final JunctionType MSL_B_SR_CF;
    public static final JunctionType MSL_B_RL_CF;
    public static final JunctionType MSL_B_CRF;
    public static final JunctionType MSL_BR_SL_CF;
    public static final JunctionType MSL_BR_SR_CF;
    public static final JunctionType MSL_BR_RL_CF;
    public static final JunctionType ML_CF;
    public static final JunctionType ML_SL_CF;
    public static final JunctionType ML_CLF;
    public static final JunctionType ML_SR_CF;
    public static final JunctionType ML_RL_CF;
    public static final JunctionType ML_CRF;
    public static final JunctionType ML_R_SL_CF;
    public static final JunctionType ML_R_SR_CF;
    public static final JunctionType ML_R_RL_CF;

    static {
        T = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD);
        LT = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD_DOWN);
        MSR_T = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD);
        MSR_LT = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN);
        MSR_B_T = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT, Orientation.FORWARD);
        MSR_B_LT = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT, Orientation.FORWARD_DOWN);
        MSL_T = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD);
        MSL_LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD_DOWN);
        MSL_B_T = new JunctionType(Orientation.LEFT, Orientation.RIGHT_DOWN, Orientation.FORWARD);
        MSL_B_LT = new JunctionType(Orientation.LEFT, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN);
        MLR_T = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD);
        MLR_LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN);
        MLR_B_T = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT_DOWN, Orientation.FORWARD);
        MLR_B_LT = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN);
        ML_T = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD);
        ML_LT = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN);
        F = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD, Orientation.BACKWARD);
        SLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD_DOWN, Orientation.BACKWARD);
        LF = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD_DOWN, Orientation.BACKWARD_DOWN);
        SRF = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD_UP, Orientation.BACKWARD);
        RLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT, Orientation.FORWARD_UP, Orientation.BACKWARD_DOWN);
        MSR_F = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD, Orientation.BACKWARD);
        MSR_SLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MSR_LF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD_DOWN);
        MSR_SRF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MSR_RLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_UP, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        MSR_B_SLF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MSR_B_SRF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MSR_B_RLF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        MSL_F = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD, Orientation.BACKWARD);
        MSL_SLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MSL_LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD_DOWN);
        MSL_SRF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MSL_RLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        MSL_B_SLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MSL_B_SRF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MSL_B_RLF = new JunctionType(Orientation.LEFT, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        MLR_F = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD,
                Orientation.BACKWARD);
        MLR_SLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MLR_LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD_DOWN);
        MLR_SRF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MLR_RLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_UP, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        MLR_B_SLF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        MLR_B_SRF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        MLR_B_RLF = new JunctionType(Orientation.LEFT_UP, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        ML_F = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD,
                Orientation.BACKWARD);
        ML_SLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD);
        ML_LF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD_DOWN,
                Orientation.BACKWARD_DOWN);
        ML_SRF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD);
        ML_RLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.RIGHT_DOWN, Orientation.FORWARD_UP,
                Orientation.BACKWARD_DOWN);
        CT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT);
        CLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_DOWN);
        CRT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_UP);
        R_CT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.BACKWARD);
        R_CLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.BACKWARD_DOWN);
        R_CRT = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.BACKWARD_UP);
        MSL_CT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT);
        MSL_CLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN);
        MSL_CRT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_UP);
        MSL_R_CT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.BACKWARD);
        MSL_R_CLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.BACKWARD_DOWN);
        MSL_R_CRT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.BACKWARD_UP);
        MSL_B_CT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT);
        MSL_B_CLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN);
        MSL_B_CRT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP);
        MSL_BR_CT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.BACKWARD);
        MSL_BR_CLT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.BACKWARD_DOWN);
        MSL_BR_CRT = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.BACKWARD_UP);
        ML_CT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT);
        ML_CLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN);
        ML_CRT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP);
        ML_R_CT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.BACKWARD);
        ML_R_CLT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.BACKWARD_DOWN);
        ML_R_CRT = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.BACKWARD_UP);
        CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD);
        SL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_DOWN, Orientation.BACKWARD);
        CLF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        SR_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_UP, Orientation.BACKWARD);
        RL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_UP,
                Orientation.BACKWARD_DOWN);
        CRF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_UP, Orientation.BACKWARD_UP);
        R_SL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD_DOWN);
        R_SR_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD_UP);
        R_RL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_UP);
        MSL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT, Orientation.BACKWARD);
        MSL_SL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        MSL_CLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        MSL_SR_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_UP,
                Orientation.BACKWARD);
        MSL_RL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_UP,
                Orientation.BACKWARD_DOWN);
        MSL_CRF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_UP,
                Orientation.BACKWARD_UP);
        MSL_R_SL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        MSL_R_SR_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT,
                Orientation.BACKWARD_UP);
        MSL_R_RL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_UP);
        MSL_B_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD);
        MSL_B_SL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        MSL_B_CLF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        MSL_B_SR_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD);
        MSL_B_RL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD_DOWN);
        MSL_B_CRF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD_UP);
        MSL_BR_SL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        MSL_BR_SR_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD_UP);
        MSL_BR_RL_CF = new JunctionType(Orientation.LEFT, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_UP);
        ML_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD);
        ML_SL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD);
        ML_CLF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_DOWN);
        ML_SR_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD);
        ML_RL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD_DOWN);
        ML_CRF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_UP,
                Orientation.BACKWARD_UP);
        ML_R_SL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD_DOWN);
        ML_R_SR_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT,
                Orientation.BACKWARD_UP);
        ML_R_RL_CF = new JunctionType(Orientation.LEFT_DOWN, Orientation.FORWARD_DOWN, Orientation.RIGHT_DOWN,
                Orientation.BACKWARD_UP);

        allTypes = ImmutableMap.<Integer, JunctionType>builder()
                .put(T.encode(), T).put(LT.encode(), LT).put(MSR_T.encode(), MSR_T).put(MSR_LT.encode(), MSR_LT)
                .put(MSR_B_T.encode(), MSR_B_T).put(MSR_B_LT.encode(), MSR_B_LT)
                .put(MSL_T.encode(), MSL_T).put(MSL_LT.encode(), MSL_LT).put(MSL_B_T.encode(), MSL_B_T)
                .put(MSL_B_LT.encode(), MSL_B_LT).put(MLR_T.encode(), MLR_T).put(MLR_LT.encode(), MLR_LT)
                .put(MLR_B_T.encode(), MLR_B_T).put(MLR_B_LT.encode(), MLR_B_LT)
                .put(ML_T.encode(), ML_T).put(ML_LT.encode(), ML_LT)
                .put(F.encode(), F).put(SLF.encode(), SLF).put(LF.encode(), LF).put(SRF.encode(), SRF)
                .put(RLF.encode(), RLF).put(MSR_F.encode(), MSR_F).put(MSR_SLF.encode(), MSR_SLF)
                .put(MSR_LF.encode(), MSR_LF).put(MSR_SRF.encode(), MSR_SRF)
                .put(MSR_RLF.encode(), MSR_RLF).put(MSR_B_SLF.encode(), MSR_B_SLF)
                .put(MSR_B_SRF.encode(), MSR_B_SRF).put(MSR_B_RLF.encode(), MSR_B_RLF)
                .put(MSL_F.encode(), MSL_F).put(MSL_SLF.encode(), MSL_SLF).put(MSL_LF.encode(), MSL_LF)
                .put(MSL_SRF.encode(), MSL_SRF).put(MSL_RLF.encode(), MSL_RLF)
                .put(MSL_B_SLF.encode(), MSL_B_SLF).put(MSL_B_SRF.encode(), MSL_B_SRF)
                .put(MSL_B_RLF.encode(), MSL_B_RLF).put(MLR_F.encode(), MLR_F)
                .put(MLR_SLF.encode(), MLR_SLF).put(MLR_LF.encode(), MLR_LF)
                .put(MLR_SRF.encode(), MLR_SRF).put(MLR_RLF.encode(), MLR_RLF)
                .put(MLR_B_SLF.encode(), MLR_B_SLF).put(MLR_B_SRF.encode(), MLR_B_SRF)
                .put(MLR_B_RLF.encode(), MLR_B_RLF).put(ML_F.encode(), ML_F).put(ML_SLF.encode(), ML_SLF)
                .put(ML_LF.encode(), ML_LF).put(ML_SRF.encode(), ML_SRF).put(ML_RLF.encode(), ML_RLF)
                .put(CT.encode(), CT).put(CLT.encode(), CLT).put(CRT.encode(), CRT)
                .put(R_CT.encode(), R_CT).put(R_CLT.encode(), R_CLT).put(R_CRT.encode(), R_CRT)
                .put(MSL_CT.encode(), MSL_CT).put(MSL_CLT.encode(), MSL_CLT).put(MSL_CRT.encode(), MSL_CRT)
                .put(MSL_R_CT.encode(), MSL_R_CT).put(MSL_R_CLT.encode(), MSL_R_CLT)
                .put(MSL_R_CRT.encode(), MSL_R_CRT).put(MSL_B_CT.encode(), MSL_B_CT)
                .put(MSL_B_CLT.encode(), MSL_B_CLT).put(MSL_B_CRT.encode(), MSL_B_CRT)
                .put(MSL_BR_CT.encode(), MSL_BR_CT).put(MSL_BR_CLT.encode(), MSL_BR_CLT)
                .put(MSL_BR_CRT.encode(), MSL_BR_CRT).put(ML_CT.encode(), ML_CT)
                .put(ML_CLT.encode(), ML_CLT).put(ML_CRT.encode(), ML_CRT)
                .put(ML_R_CT.encode(), ML_R_CT).put(ML_R_CLT.encode(), ML_R_CLT)
                .put(ML_R_CRT.encode(), ML_R_CRT)
                .put(CF.encode(), CF).put(SL_CF.encode(), SL_CF).put(CLF.encode(), CLF)
                .put(SR_CF.encode(), SR_CF).put(RL_CF.encode(), RL_CF)
                .put(CRF.encode(), CRF).put(R_SL_CF.encode(), R_SL_CF).put(R_SR_CF.encode(), R_SR_CF)
                .put(R_RL_CF.encode(), R_RL_CF).put(MSL_CF.encode(), MSL_CF)
                .put(MSL_SL_CF.encode(), MSL_SL_CF).put(MSL_CLF.encode(), MSL_CLF)
                .put(MSL_SR_CF.encode(), MSL_SR_CF).put(MSL_RL_CF.encode(), MSL_RL_CF)
                .put(MSL_CRF.encode(), MSL_CRF).put(MSL_R_SL_CF.encode(), MSL_R_SL_CF)
                .put(MSL_R_SR_CF.encode(), MSL_R_SR_CF).put(MSL_R_RL_CF.encode(), MSL_R_RL_CF)
                .put(MSL_B_CF.encode(), MSL_B_CF).put(MSL_B_SL_CF.encode(), MSL_B_SL_CF)
                .put(MSL_B_CLF.encode(), MSL_B_CLF).put(MSL_B_SR_CF.encode(), MSL_B_SR_CF)
                .put(MSL_B_RL_CF.encode(), MSL_B_RL_CF).put(MSL_B_CRF.encode(), MSL_B_CRF)
                .put(MSL_BR_SL_CF.encode(), MSL_BR_SL_CF).put(MSL_BR_SR_CF.encode(), MSL_BR_SR_CF)
                .put(MSL_BR_RL_CF.encode(), MSL_BR_RL_CF).put(ML_CF.encode(), ML_CF)
                .put(ML_SL_CF.encode(), ML_SL_CF).put(ML_CLF.encode(), ML_CLF)
                .put(ML_SR_CF.encode(), ML_SR_CF).put(ML_RL_CF.encode(), ML_RL_CF)
                .put(ML_CRF.encode(), ML_CRF).put(ML_R_SL_CF.encode(), ML_R_SR_CF)
                .put(ML_R_RL_CF.encode(), ML_R_RL_CF).build();
    }

    private final Pair<Pair<Orientation, Boolean>, Pair<Orientation, Boolean>> mainOrientation;
    private final Pair<Orientation, Boolean> other1Orientation;
    private final Pair<Orientation, Boolean> other2Orientation;
    private final Map<Orientation, Set<Orientation>> restrictions;
    private final RUID main;
    private final RUID other1;
    private final RUID other2;
    private final Rotation rotation;

    private static Field mainOrientationField;
    private static Field other1OrientationField;
    private static Field other2OrientationField;
    private static Field restrictionsField;
    private static Field mainField;
    private static Field other1Field;
    private static Field other2Field;
    private static Field rotationField;

    static {
        try {
            mainOrientationField = JunctionType.class.getDeclaredField("mainOrientation");
            other1OrientationField = JunctionType.class.getDeclaredField("other1Orientation");
            other2OrientationField = JunctionType.class.getDeclaredField("other1Orientation");
            restrictionsField = JunctionType.class.getDeclaredField("restrictions");
            mainField = JunctionType.class.getDeclaredField("main");
            other1Field = JunctionType.class.getDeclaredField("other1");
            other2Field = JunctionType.class.getDeclaredField("other2");
            rotationField = JunctionType.class.getDeclaredField("rotation");
            mainOrientationField.setAccessible(true);
            other1OrientationField.setAccessible(true);
            other2OrientationField.setAccessible(true);
            restrictionsField.setAccessible(true);
            mainField.setAccessible(true);
            other1Field.setAccessible(true);
            other2Field.setAccessible(true);
            rotationField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            System.err.println("ERROR: The following should not have occurred. Recommend disabling Railroad plugin.");
            e.printStackTrace();
        }
    }

    public JunctionType() {
        this.mainOrientation = null;
        this.other1Orientation = null;
        this.other2Orientation = null;
        this.restrictions = null;
        this.main = null;
        this.other1 = null;
        this.other2 = null;
        this.rotation = Rotation.NONE;
    }

    private JunctionType(@NotNull Orientation mainFirst, @NotNull Orientation mainSecond,
                           @NotNull Orientation other1) {
        this(mainFirst, mainSecond, other1, null);
    }


    private JunctionType(@NotNull Orientation mainFirst, @NotNull Orientation mainSecond, @NotNull Orientation other1,
                           @Nullable Orientation other2) {
        this.mainOrientation = new Pair<>(new Pair<>(mainFirst, mainFirst.isDown()),
                new Pair<>(mainSecond, mainSecond.isDown()));
        this.other1Orientation = new Pair<>(other1, other1.isDown());
        if (other2 == null)
            this.other2Orientation = null;
        else
            this.other2Orientation = new Pair<>(other2, other2.isDown());
        this.main = null;
        this.other1 = null;
        this.other2 = null;
        this.rotation = Rotation.NONE;

        this.restrictions = new HashMap<>();
        if (mainFirst.isUp()) {
            Orientation l = mainFirst.rotateCC();
            Orientation r = mainFirst.rotateC();
            if (l.isSameDirection(other1) || r.isSameDirection(other1))
                this.setRestrictions(mainFirst, other1);
            if (other2 != null && (l.isSameDirection(other2) || r.isSameDirection(other2)))
                this.setRestrictions(mainFirst, other2);
        } else if (mainSecond.isUp()) {
            Orientation l = mainSecond.rotateCC();
            Orientation r = mainSecond.rotateC();
            if (l.isSameDirection(other1) || r.isSameDirection(other1))
                this.setRestrictions(mainSecond, other1);
            if (other2 != null && (l.isSameDirection(other2) || r.isSameDirection(other2)))
                this.setRestrictions(mainSecond, other2);
        }
        if (other1.isUp()) {
            Orientation l = other1.rotateCC();
            Orientation r = other1.rotateC();
            if (!mainFirst.isUp() && (l.isSameDirection(mainFirst) || r.isSameDirection(mainFirst)))
                this.setRestrictions(other1, mainFirst);
            if (!mainSecond.isUp() && (l.isSameDirection(mainSecond) || r.isSameDirection(mainSecond)))
                this.setRestrictions(other1, mainSecond);
            if (other2 != null && (l.isSameDirection(other2) || r.isSameDirection(other2)))
                this.setRestrictions(other1, other2);
        }
        if (other2 != null && other2.isUp()) {
            Orientation l = other2.rotateCC();
            Orientation r = other2.rotateC();
            if (!mainFirst.isUp() && (l.isSameDirection(mainFirst) || r.isSameDirection(mainFirst)))
                this.setRestrictions(other2, mainFirst);
            if (!mainSecond.isUp() && (l.isSameDirection(mainSecond) || r.isSameDirection(mainSecond)))
                this.setRestrictions(other2, mainSecond);
            if (!other1.isUp() && (l.isSameDirection(other1) || r.isSameDirection(other1)))
                this.setRestrictions(other2, other1);
        }
    }

    public static @NotNull JunctionType createJunctionType(@NotNull Location loc, @NotNull ITrack main,
                                                           @NotNull ITrack other1, @Nullable ITrack other2)
            throws InvalidJunctionLocationException {

        Orientation mainFirstOrient = null;
        Orientation mainSecondOrient = null;
        Orientation other1Orient = null;
        Orientation other2Orient = null;
        boolean other1First = false;

        if (loc.getBlock().getType() != Material.RAIL)
            throw new InvalidJunctionLocationException("The location given does not contain a normal rail material.");
        Orientation orient = Orientation.LEFT;
        Location var;
        for (int i = 0; i < 4; i++) {
            var = loc.clone();
            var.add(orient.getDirection().getVector());

            boolean found = false;
            if (Rail.materialIsRail(var.getBlock().getType())) {
                if (mainFirstOrient == null || mainSecondOrient == null) {
                    if (main.contains(var)) {
                        if (mainFirstOrient == null)
                            mainFirstOrient = orient;
                        else
                            mainSecondOrient = orient;
                        found = true;
                    }
                } else {
                    if (other1Orient == null && other1.contains(var)) {
                        other1Orient = orient;
                        found = true;
                        if (other2 != null && other2Orient == null)
                            other1First = true;
                    } else if (other2 != null && other2Orient == null && other2.contains(var)) {
                        other2Orient = orient;
                        found = true;
                    }
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

        if (mainFirstOrient == null || mainSecondOrient == null || other1Orient == null ||
            (other2 != null && other2Orient == null))
            throw new InvalidJunctionLocationException("Some of the tracks were not found.");
        if (other1Orient.isDown()) {
            Location railLoc = loc.clone().add(other1Orient.getDirection().getVector());
            Rail rail = other1.getRail(railLoc);
            if (rail == null)
                throw new RailroadException("ITrack: " + other1.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Couldn't find a rail at this location.");
            if (!other1.getStart().equals(rail) && !other1.getEnd().equals(rail) &&
                rail.getDefaultShape().isCurved())
                throw new InvalidJunctionLocationException("ITrack: " + other1.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Cannot connect to junction from below at a corner of the track.");
        }
        if (other2 != null && other2Orient.isDown()) {
            Location railLoc = loc.clone().add(other2Orient.getDirection().getVector());
            Rail rail = other2.getRail(railLoc);
            if (rail == null)
                throw new RailroadException("ITrack: " + other2.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Couldn't find a rail at this location.");
            if (!other2.getStart().equals(rail) && !other2.getEnd().equals(rail) &&
                rail.getDefaultShape().isCurved())
                throw new InvalidJunctionLocationException("ITrack: " + other2.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Cannot connect to junction from below at a corner of the track.");
        }

        int orig, rotCC, flip, rotC;
        if (other2 != null && !other1First) {
            orig = encode(mainFirstOrient, mainSecondOrient, other2Orient, other1Orient);
            rotCC = encode(mainFirstOrient.rotateCC(), mainSecondOrient.rotateCC(), other2Orient.rotateCC(),
                    other1Orient.rotateCC());
            flip = encode(mainFirstOrient.rotate180(), mainSecondOrient.rotate180(), other2Orient.rotate180(),
                    other1Orient.rotate180());
            rotC = encode(mainFirstOrient.rotateC(), mainSecondOrient.rotateC(), other2Orient.rotateC(),
                    other1Orient.rotateC());

            Orientation orientTemp = other1Orient;
            ITrack trackTemp = other1;
            other1Orient = other2Orient;
            other1 = other2;
            other2Orient = orientTemp;
            other2 = trackTemp;
        } else if (other2 != null) {
            orig = encode(mainFirstOrient, mainSecondOrient, other1Orient, other2Orient);
            rotCC = encode(mainFirstOrient.rotateCC(), mainSecondOrient.rotateCC(), other1Orient.rotateCC(),
                    other2Orient.rotateCC());
            flip = encode(mainFirstOrient.rotate180(), mainSecondOrient.rotate180(), other1Orient.rotate180(),
                    other2Orient.rotate180());
            rotC = encode(mainFirstOrient.rotateC(), mainSecondOrient.rotateC(), other1Orient.rotateC(),
                    other2Orient.rotateC());
        } else {
            orig = encode(mainFirstOrient, mainSecondOrient, other1Orient, null);
            rotCC = encode(mainFirstOrient.rotateCC(), mainSecondOrient.rotateCC(), other1Orient.rotateCC(), null);
            flip = encode(mainFirstOrient.rotate180(), mainSecondOrient.rotate180(), other1Orient.rotate180(), null);
            rotC = encode(mainFirstOrient.rotateC(), mainSecondOrient.rotateC(), other1Orient.rotateC(), null);
        }

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
            mainField.set(junctionType, main.getRUID());
            other1Field.set(junctionType, other1.getRUID());
            other2Field.set(junctionType, other2 == null ? null : other2.getRUID());
            rotationField.set(junctionType, rotation);
        } catch (IllegalAccessException e) {
            throw new InvalidJunctionLocationException("Creating a JunctionType failed.", e);
        }

        if (main.getWay() != ITrack.Way.BOTH_WAYS) {
            Rail rail = main.getRail(loc);
            if (rail == null)
                throw new RailroadException("ITrack: " + main.getRUID().toString() + " Location: " +
                        loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() +
                        " Error: Couldn't find a rail at this location.");
            if (mainFirstOrient.getRegular().getDirection() == rail.getStartDirection()) {
                if (main.getWay() == ITrack.Way.ONE_WAY_FROM) {
                    junctionType.setRestrictions(mainFirstOrient, other1Orient);
                    if (other2 != null)
                        junctionType.setRestrictions(mainFirstOrient, other2Orient);
                } else {
                    junctionType.setRestrictions(mainSecondOrient, other1Orient);
                    if (other2 != null)
                        junctionType.setRestrictions(mainSecondOrient, other2Orient);
                }
            } else {
                if (main.getWay() == ITrack.Way.ONE_WAY_FROM) {
                    junctionType.setRestrictions(mainSecondOrient, other1Orient);
                    if (other2 != null)
                        junctionType.setRestrictions(mainSecondOrient, other2Orient);
                } else {
                    junctionType.setRestrictions(mainFirstOrient, other1Orient);
                    if (other2 != null)
                        junctionType.setRestrictions(mainFirstOrient, other2Orient);
                }
            }
        }

        if (other1.getWay() != ITrack.Way.BOTH_WAYS) {
            Location railLoc = loc.clone().add(other1Orient.getDirection().getVector());
            Rail rail = other1.getRail(railLoc);
            if (rail == null)
                throw new RailroadException("ITrack: " + other1.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Couldn't find a rail at this location.");
            if (other1.getStart().equals(rail) && other1.getWay() == ITrack.Way.ONE_WAY_TO) {
                junctionType.setRestriction(mainFirstOrient, other1Orient);
                junctionType.setRestriction(mainSecondOrient, other1Orient);
                if (other2 != null)
                    junctionType.setRestriction(other2Orient, other1Orient);
            } else if (other1.getEnd().equals(rail) && other1.getWay() == ITrack.Way.ONE_WAY_FROM) {
                junctionType.setRestriction(mainFirstOrient, other1Orient);
                junctionType.setRestriction(mainSecondOrient, other1Orient);
                if (other2 != null)
                    junctionType.setRestriction(other2Orient, other1Orient);
            }
        }
        if (other2 != null && other2.getWay() != ITrack.Way.BOTH_WAYS) {
            Location railLoc = loc.clone().add(other2Orient.getDirection().getVector());
            Rail rail = other2.getRail(railLoc);
            if (rail == null)
                throw new RailroadException("ITrack: " + other2.getRUID().toString() + " Location: " +
                        railLoc.getBlockX() + "," + railLoc.getBlockY() + "," + railLoc.getBlockZ() +
                        " Error: Couldn't find a rail at this location.");
            if (other2.getStart().equals(rail) && other2.getWay() == ITrack.Way.ONE_WAY_TO) {
                junctionType.setRestriction(mainFirstOrient, other2Orient);
                junctionType.setRestriction(mainSecondOrient, other2Orient);
                junctionType.setRestriction(other1Orient, other2Orient);
            } else if (other2.getEnd().equals(rail) && other2.getWay() == ITrack.Way.ONE_WAY_FROM) {
                junctionType.setRestriction(mainFirstOrient, other2Orient);
                junctionType.setRestriction(mainSecondOrient, other2Orient);
                junctionType.setRestriction(other1Orient, other2Orient);
            }
        }

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

    public boolean isRestricted(RUID from, RUID to) {
        Orientation orientFrom;
        Orientation orientTo;

        if (!(from.isValid() && to.isValid()))
            return false;
        if (main.equals(from)) {

        }
        return false;
    }

    public boolean needsChange(RUID side) {
        return false;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        JunctionType type = (JunctionType) super.clone();
        if (type.mainOrientation == null)
            return type;
        try {
            mainOrientationField.set(type, new Pair<>(
                    new Pair<>(type.mainOrientation.getFirst().getFirst(),
                            type.mainOrientation.getFirst().getSecond()),
                    new Pair<>(type.mainOrientation.getSecond().getFirst(),
                            type.mainOrientation.getSecond().getSecond())
            ));
            other1OrientationField.set(type, new Pair<>(
                    type.other1Orientation.getFirst(),
                    type.other1Orientation.getSecond()
            ));
            if (type.other2Orientation != null)
                other2OrientationField.set(type, new Pair<>(
                        type.other2Orientation.getFirst(),
                        type.other2Orientation.getSecond()
                ));
            Map<Orientation, Set<Orientation>> newRestrictions = new HashMap<>();
            for (Map.Entry<Orientation, Set<Orientation>> entry : type.restrictions.entrySet())
                newRestrictions.put(entry.getKey(), new HashSet<>(entry.getValue()));
            restrictionsField.set(type, newRestrictions);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new JunctionType();
        }

        return type;
    }

    public int encode() {
        int code = mainOrientation.getFirst().getFirst().encode() + this.rotation.encode();
        code = mainOrientation.getSecond().getFirst().encode() + code << 8;
        code = other1Orientation.getFirst().encode() + code << 8;
        code = (other2 == null ? 0 : other2Orientation.getFirst().encode()) + code << 8;
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
        if (!this.mainEquals(object))
            return false;
        return this.rotation == object.rotation;
    }

    public boolean equalsIgnoreRotation(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof JunctionType))
            return false;
        JunctionType object = (JunctionType) o;
        return this.mainEquals(object);
    }

    private boolean mainEquals(JunctionType object) {
        if (this.main != object.main)
            return false;
        if (this.other1 != object.other1)
            return false;
        if (this.other2 != object.other2)
            return false;
        if (this.mainOrientation.getFirst().getFirst() != object.mainOrientation.getFirst().getFirst())
            return false;
        if (!this.mainOrientation.getFirst().getSecond().equals(object.mainOrientation.getFirst().getSecond()))
            return false;
        if (this.mainOrientation.getSecond().getFirst() != object.mainOrientation.getSecond().getFirst())
            return false;
        if (!this.mainOrientation.getSecond().getSecond().equals(object.mainOrientation.getSecond().getSecond()))
            return false;
        if (this.other1Orientation.getFirst() != object.other1Orientation.getFirst())
            return false;
        if (!this.other1Orientation.getSecond().equals(object.other1Orientation.getSecond()))
            return false;
        if (this.other2Orientation.getFirst() != object.other2Orientation.getFirst())
            return false;
        return this.other2Orientation.getSecond().equals(object.other2Orientation.getSecond());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        this.writeHeader(out);



        this.writeFooter(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    private void writeHeader(ObjectOutput out) throws IOException {
        out.writeChar('{');
        out.writeObject("JunctionType");
        out.writeObject(Railroad.version);
    }

    private void writeFooter(ObjectOutput out) throws IOException {
        out.writeChar('}');
    }
}
