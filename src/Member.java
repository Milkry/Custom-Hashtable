/**
 *
 * @author Panagiotis Stathopoulos (19064087)
 *
 */

public class Member implements IMember {
    String fullname;
    String affiliation;

    public Member(String fullname, String affiliation) {
        this.fullname = fullname;
        this.affiliation = affiliation;
    }

    /**
     * Retrieves the name of the member
     *
     * @return the name of the member
     * @pre true
     */
    @Override
    public String getName() {
        return fullname;
    }

    /**
     * Retrieves the member's affiliation
     *
     * @return the member's affiliation
     * @pre true
     */
    @Override
    public String getAffiliation() {
        return affiliation;
    }

    @Override
    /**
     * @return the member's name and affiliation as string
     */
    public String toString() {
        return fullname + " <> " + affiliation;
    }
}
