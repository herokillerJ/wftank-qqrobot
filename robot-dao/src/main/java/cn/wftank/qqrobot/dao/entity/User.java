package cn.wftank.qqrobot.dao.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wftank
 * @since 2021-02-27
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name=" + name +
        "}";
    }
}
