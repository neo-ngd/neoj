package DNA.IO.Caching;

import java.util.*;
import java.util.function.IntFunction;

public class TrackableCollection<TKey, TItem extends ITrackable<TKey>> extends AbstractCollection<TItem> {
	private HashMap<TKey, TItem> map = new HashMap<TKey, TItem>();
	
    public TrackableCollection() { }

    public TrackableCollection(TItem[] items) {
        for (TItem item : items) {
        	this.add(item);
            item.setTrackState(TrackState.None);
        }
    }
    
    @Override
	public void clear() {
    	map.clear();
    }
    
    @Override
    public boolean add(TItem e) {
    	e.setTrackState(TrackState.Added);
    	return map.put(e.key(), e) == null;
    }
    
    public boolean remove(TItem key) {
    	TItem item = map.get(key);
    	if (item == null) {
    		return false;
    	}
        if (item.getTrackState() == TrackState.Added) {
            map.remove(key);
        } else {
        	item.setTrackState(TrackState.Deleted);
        }
        return true;
    }

    public void commit() {
    	Iterator<TItem> iterator = map.values().iterator();
    	while (iterator.hasNext()) {
    		TItem item = iterator.next();
            if (item.getTrackState() == TrackState.Deleted) {
            	iterator.remove();
            } else {
            	item.setTrackState(TrackState.None);
            }
    	}
    }
    
    public boolean containsKey(TKey key) {
    	return map.containsKey(key);
    }
    
    public boolean containsValue(TItem item) {
    	return containsKey(item.key());
    }
    
    public TItem get(TKey key) {
    	return map.get(key);
    }
    
	public TItem[] getChangeSet(IntFunction<TItem[]> generator) {
    	return map.values().stream().filter(p -> p.getTrackState() != TrackState.None).toArray(generator);
    }
    
    @Override
    public Iterator<TItem> iterator() {
    	return map.values().iterator();
    }
    
    @Override
    public int size() {
    	return map.size();
    }
}