package boot.domain;

import java.util.Set;
import java.util.TreeSet;

public class Job {
    private String title;
    private String company;
    private Set<String> tags = new TreeSet<>();
    private int exp;

    public Job() {
    }

    public Job(String title, String company, Set<String> tags, int exp) {
        this.title = title;
        this.company = company;
        setTags(tags);
        this.exp = exp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Set<String> getTags() {
        return new TreeSet<>(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = new TreeSet<>();
        if (tags != null) {
            this.tags.addAll(tags);
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
        return title + " at " + company;
    }
}
