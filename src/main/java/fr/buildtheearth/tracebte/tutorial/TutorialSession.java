package fr.buildtheearth.tracebte.tutorial;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TutorialSession {

    private final UUID playerId;
    private TutorialStep step;
    private int tpllCount;
    private int lineCount;
    private final Set<Integer> validatedCorners;
    private final Set<Location> placedBlocks;

    public TutorialSession(UUID playerId) {
        this.playerId = playerId;
        this.step = TutorialStep.INTRO;
        this.tpllCount = 0;
        this.lineCount = 0;
        this.validatedCorners = new HashSet<>();
        this.placedBlocks = new HashSet<>();
    }

    public UUID getPlayerId()                  { return playerId; }
    public TutorialStep getStep()              { return step; }
    public void setStep(TutorialStep step)     { this.step = step; }
    public int getTpllCount()                  { return tpllCount; }
    public void incrementTpll()                { tpllCount++; }
    public int getLineCount()                  { return lineCount; }
    public void incrementLine()                { lineCount++; }
    public Set<Integer> getValidatedCorners()  { return validatedCorners; }
    public Set<Location> getPlacedBlocks()     { return placedBlocks; }
    public boolean allCornersPlaced()          { return validatedCorners.size() >= 4; }
}
