package student.op.mehelp;

/**
 * Created by varun on 2017-09-23.
 */

public class PersonalInfo {
    public Integer id;
    public String name;
    public String address;
    public String phone;
    public String blood;
    public String foodrisk;
    public byte[] image;

    public PersonalInfo(Integer id,
                        String name,
                        String address,
                        String phone,
                        String blood,
                        String foodrisk,
                        byte[] image) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.blood = blood;
        this.foodrisk = foodrisk;
        this.image = image;
    }
}
