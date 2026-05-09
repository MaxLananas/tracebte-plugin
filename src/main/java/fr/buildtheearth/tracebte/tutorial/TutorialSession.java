package fr.buildtheearth.tracebte.tutorial;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TutorialSession {

    private final UUID playerId;
    private TutorialStep step;
    private int tpllCount;
    private final Set<Integer> validatedCorners;

    private boolean pendingTpll = false;
    private double pendingLat;
    private double pendingLon;

    public TutorialSession(UUID playerId) {
        this.playerId = playerId;
        this.step = TutorialStep.INTRO;
        this.tpllCount = 0;
        this.validatedCorners = new HashSet<>();
    }

    public UUID getPlayerId() { return playerId; }

    public TutorialStep getStep() { return step; }
    public void setStep(TutorialStep step) { this.step = step; }

    public int getTpllCount() { return tpllCount; }
    public void incrementTpll() { tpllCount++; }

    public Set<Integer> getValidatedCorners() { return validatedCorners; }
    public boolean allCornersPlaced() { return validatedCorners.size() >= 4; }

    public void setPendingTpll(double lat, double lon) {
        this.pendingTpll = true;
        this.pendingLat = lat;
        this.pendingLon = lon;
    }

    public boolean hasPendingTpll() { return pendingTpll; }
    public void clearPendingTpll() { this.pendingTpll = false; }
}