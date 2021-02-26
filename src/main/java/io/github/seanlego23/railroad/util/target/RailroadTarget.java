package io.github.seanlego23.railroad.util.target;

import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;

public interface RailroadTarget extends Removable, Externalizable { //ID

	@NotNull RUID getID();

}
