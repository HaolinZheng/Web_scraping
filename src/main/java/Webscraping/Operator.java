package Webscraping;

import java.util.List;

public class Operator {
    private String name, position, attack;
    private boolean alter;
    private List<Skill> skill;
    private Class clase;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getAttack() {
        return attack;
    }

    public boolean isAlter() {
        return alter;
    }

    public List<Skill> getSkill() {
        return skill;
    }

    public Class getClase() {
        return clase;
    }

    public Operator(String name, String position, String attack, boolean alter, List<Skill> skill, Class clase) {
        this.name = name;
        this.position = position;
        this.attack = attack;
        this.alter = alter;
        this.skill = skill;
        this.clase = clase;
    }
}