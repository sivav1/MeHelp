package student.op.mehelp;

/**
 * Created by varun on 2017-09-18.
 */

public class Contact {
    public int id;
    public String name;
    public String phoneNumber;
    public String relation;
    public byte[] image;

    public Contact(int id, String name, String phoneNumber, String relation, byte[] image) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relation = relation;
        this.image = image;
    }
}
