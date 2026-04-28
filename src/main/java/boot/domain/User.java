package boot.domain;

import java.util.Set;
import java.util.TreeSet;

public class User {
    private String name;
    private Set<String> skills = new TreeSet<>();
    private int exp;

    public User() {
    }

    public User(String name, Set<String> skills, int exp) {
        this.name = name;
        setSkills(skills);
        this.exp = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSkills() {
        return new TreeSet<>(skills);
    }

    public void setSkills(Set<String> skills) {
        this.skills = new TreeSet<>();
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return name + " " + String.join(",", skills) + " " + exp;
    }
}
