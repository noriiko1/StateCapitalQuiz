package edu.uga.cs.statecapitalsquiz.model;

/**
 * Represents one row of the states table: a US state, its capital, two
 * other plausible-but-incorrect cities, and the year the state joined the Union.
 */
public class StateInfo {

    private long id;
    private String stateName;
    private String capital;
    private String city2;
    private String city3;
    private int statehoodYear;

    public StateInfo(long id, String stateName, String capital, String city2,
                      String city3, int statehoodYear) {
        this.id = id;
        this.stateName = stateName;
        this.capital = capital;
        this.city2 = city2;
        this.city3 = city3;
        this.statehoodYear = statehoodYear;
    }

    public long getId() {
        return id;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCapital() {
        return capital;
    }

    public String getCity2() {
        return city2;
    }

    public String getCity3() {
        return city3;
    }

    public int getStatehoodYear() {
        return statehoodYear;
    }
}
