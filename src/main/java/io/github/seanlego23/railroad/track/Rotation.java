package io.github.seanlego23.railroad.track;

public enum Rotation {
    NONE,
    CC90,
    FLIP,
    C90;

    public Rotation rotateCC() {
        switch (this) {
            case NONE:
                return CC90;
            case CC90:
                return FLIP;
            case FLIP:
                return C90;
            default:
                return NONE;
        }
    }

    public Rotation rotateC() {
        switch (this) {
            case NONE:
                return C90;
            case CC90:
                return NONE;
            case FLIP:
                return CC90;
            default:
                return FLIP;
        }
    }

    public Rotation flip() {
        switch (this) {
            case NONE:
                return FLIP;
            case CC90:
                return C90;
            case FLIP:
                return NONE;
            default:
                return CC90;
        }
    }

    public byte encode() {
        switch (this) {
            case NONE:
                return 0;
            case CC90:
                return (byte) 0x40;
            case C90:
                return (byte) 0x80;
            default:
                return (byte) 0xC0;
        }
    }
}
