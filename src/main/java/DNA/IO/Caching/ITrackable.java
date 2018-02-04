package DNA.IO.Caching;

public interface ITrackable<TKey> {
    TKey key();
    TrackState getTrackState();
    void setTrackState(TrackState state);
}
