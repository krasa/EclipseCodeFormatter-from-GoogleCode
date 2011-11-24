package krasa.formatter.settings;

/**
 * @author Vojtech Krasa
 */
public class JoinedGroup {

    private final String group1;
    private final String group2;

    public String getGroup1() {
        return group1;
    }

    public String getGroup2() {
        return group2;
    }

    public JoinedGroup(String group1, String group2) {
        this.group1 = group1;
        this.group2 = group2;
    }

    public static JoinedGroup from(String group1, String group2) {
        return new JoinedGroup(group1, group2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinedGroup that = (JoinedGroup) o;

        return isSame(that);
    }

    @Override
    public int hashCode() {
        int result = group1.hashCode();
        result = 31 * result + group2.hashCode();
        return result;
    }

    private boolean isSame(JoinedGroup that) {
        boolean b = group1.equals(that.group1) && group2.equals(that.group2);
        boolean c = group1.equals(that.group2) && group2.equals(that.group1);
        return b || c;
    }

}
