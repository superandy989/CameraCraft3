package de.take_weiland.mods.cameracraft.api.cable;

import java.util.Collection;

import com.google.common.base.Predicate;

public interface DataNetwork {

	Collection<NetworkNode> getNodes();
	
	Collection<NetworkNode> nodesMatching(Predicate<? super NetworkNode> predicate);
	
	boolean isValid();
	
	void invalidate();
	
	void join(NetworkNode node);
	
	void leave(NetworkNode node);
	
	void joinWith(DataNetwork other);
	
	void dispatch(NetworkEvent event);
	
	void addListener(NetworkListener listener);
	
	void addListeners(Collection<NetworkListener> listeners);
	
	void removeListener(NetworkListener listener);
	
	Collection<NetworkListener> getListeners();
	
}
