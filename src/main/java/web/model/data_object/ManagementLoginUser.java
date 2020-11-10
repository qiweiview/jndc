package web.model.data_object;

public class ManagementLoginUser {
    private String name;

    private String passWord;


    @Override
    public String toString() {
        return "ManagementLoginUser{" +
                "name='" + name + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
