package Webscraping;

public class Skill {
    private String name, charge, duration;
    private int cost, initial;
    private boolean auto;

    public String getName() {
        return name;
    }

    public String getCharge() {
        return charge;
    }

    public String getDuration() {
        return duration;
    }

    public int getCost() {
        return cost;
    }

    public int getInitial() {
        return initial;
    }

    public boolean isAuto() {
        return auto;
    }

    public Skill(String name, String charge, String duration, int cost, int initial, boolean auto) {
        this.name = name;
        this.charge = charge;
        this.duration = duration;
        this.cost = cost;
        this.initial = initial;
        this.auto = auto;
    }
}
